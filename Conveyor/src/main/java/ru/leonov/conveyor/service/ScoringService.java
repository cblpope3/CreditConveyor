package ru.leonov.conveyor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.leonov.conveyor.dto.ModelsCreditDTO;
import ru.leonov.conveyor.dto.ModelsEmploymentDTO;
import ru.leonov.conveyor.dto.ModelsScoringDataDTO;
import ru.leonov.conveyor.exceptions.ScoringException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

/**
 * This service handle scoring process.
 */
@Service
public class ScoringService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final double baseRate;
    private final CreditCalculationService creditCalculationService;

    @Autowired
    public ScoringService(@Value("${app-params.baseRate}") double baseRate,
                          CreditCalculationService creditCalculationService) {
        this.baseRate = baseRate;
        this.creditCalculationService = creditCalculationService;
    }

    /**
     * Calculate credit rate and create detailed credit offer.
     *
     * @param scoringData data for requested credit scoring.
     * @return detailed credit offer.
     * @throws ScoringException if scoring data didn't pass data validation.
     */
    public ModelsCreditDTO calculateCredit(ModelsScoringDataDTO scoringData) throws ScoringException {

        BigDecimal rate = BigDecimal.valueOf(this.calculateRate(scoringData));

        return creditCalculationService.calculateCredit(scoringData.getAmount(), rate, scoringData.getTerm(),
                scoringData.getIsInsuranceEnabled(), scoringData.getIsSalaryClient());
    }

    /**
     * Calculate credit rate to credit request.
     *
     * @param scoringData data to perform scoring.
     * @return credit rate.
     * @throws ScoringException if scoring data is unacceptable to get credit.
     */
    private double calculateRate(ModelsScoringDataDTO scoringData) throws ScoringException {

        if (logger.isTraceEnabled()) logger.trace("Calculating credit rate. Base rate is {}.", baseRate);

        double resultRate = baseRate;

        resultRate += this.getJobCorrection(scoringData.getEmployment().getEmploymentStatus(),
                scoringData.getEmployment().getPosition());
        resultRate += this.getSalaryCorrection(scoringData.getAmount(), scoringData.getEmployment().getSalary());
        resultRate += this.getFamilyCorrection(scoringData.getMaritalStatus(), scoringData.getDependentAmount());
        resultRate += this.getAgeRateCorrection(scoringData.getBirthdate(), scoringData.getGender());
        resultRate += this.getExperienceRateCorrection(
                scoringData.getEmployment().getWorkExperienceTotal(),
                scoringData.getEmployment().getWorkExperienceCurrent());

        if (logger.isDebugEnabled()) logger.debug("Credit rate is calculated: {}.", resultRate);

        return resultRate;
    }

    /**
     * Calculate credit rate correction accordingly to client job.
     *
     * @param employmentStatus clients employment status.
     * @param jobPosition      clients job position.
     * @return credit rate correction coefficient.
     * @throws ScoringException if clients employment status is unacceptable to get credit.
     */
    private double getJobCorrection(ModelsEmploymentDTO.EmploymentStatusEnum employmentStatus,
                                    ModelsEmploymentDTO.PositionEnum jobPosition) throws ScoringException {

        double resultCorrection = 0;

        if (employmentStatus.equals(ModelsEmploymentDTO.EmploymentStatusEnum.UNEMPLOYED)) {
            // Безработный → отказ
            throw new ScoringException(ScoringException.ExceptionCause.UNACCEPTABLE_EMPLOYER_STATUS);
        } else if (employmentStatus.equals(ModelsEmploymentDTO.EmploymentStatusEnum.SELF_EMPLOYED)) {
            // Самозанятый → ставка увеличивается на 1
            resultCorrection += 1;
            if (logger.isTraceEnabled()) logger.trace("Credit rate is increased by 1 because self-employed.");
        } else if (employmentStatus.equals(ModelsEmploymentDTO.EmploymentStatusEnum.BUSINESS_OWNER)) {
            // Владелец бизнеса → ставка увеличивается на 3
            resultCorrection += 3;
            if (logger.isTraceEnabled()) logger.trace("Credit rate is increased by 3 because business owner.");
        }

        if (jobPosition.equals(ModelsEmploymentDTO.PositionEnum.MID_MANAGER)) {
            // Менеджер среднего звена → ставка уменьшается на 2
            resultCorrection -= 2;
            if (logger.isTraceEnabled()) logger.trace("Credit rate is decreased by 2 because mid-manager.");

        } else if (jobPosition.equals(ModelsEmploymentDTO.PositionEnum.TOP_MANAGER)) {
            // Топ-менеджер → ставка уменьшается на 4
            resultCorrection -= 4;
            if (logger.isTraceEnabled()) logger.trace("Credit rate is decreased by 4 because top-manager.");
        }

        return resultCorrection;
    }

    /**
     * Calculate credit rate correction accordingly to client salary.
     *
     * @param creditAmount amount of requested credit.
     * @param salary       customers salary.
     * @return credit rate correction coefficient.
     * @throws ScoringException if clients salary is insufficient to get credit.
     */
    private double getSalaryCorrection(BigDecimal creditAmount, BigDecimal salary) throws ScoringException {
        if (creditAmount.compareTo(salary.multiply(BigDecimal.valueOf(20))) > 0) {
            //Сумма займа больше, чем 20 зарплат → отказ
            throw new ScoringException(ScoringException.ExceptionCause.INSUFFICIENT_SALARY);
        }
        return 0;
    }

    /**
     * Calculate credit rate correction accordingly to client marital status.
     *
     * @param maritalStatus   martial status of customer.
     * @param dependentAmount amount of dependent persons (children etc.).
     * @return credit rate correction coefficient.
     */
    private double getFamilyCorrection(ModelsScoringDataDTO.MaritalStatusEnum maritalStatus, Integer dependentAmount) {

        double resultCorrection = 0;

        if (maritalStatus.equals(ModelsScoringDataDTO.MaritalStatusEnum.MARRIED)) {
            // Замужем/женат → ставка уменьшается на 3
            if (logger.isTraceEnabled()) logger.trace("Credit rate is decreased by 3 because married.");
            resultCorrection -= 3;
        } else if (maritalStatus.equals(ModelsScoringDataDTO.MaritalStatusEnum.DIVORCED)) {
            // Разведен → ставка увеличивается на 1
            if (logger.isTraceEnabled()) logger.trace("Credit rate is increased by 1 because divorced.");
            resultCorrection += 1;
        }

        if (dependentAmount > 1) {
            //Количество иждивенцев больше 1 → ставка увеличивается на 1
            resultCorrection += 1;
            if (logger.isTraceEnabled())
                logger.trace("Credit rate is increased by 1 because too much dependent persons.");
        }

        return resultCorrection;
    }

    /**
     * Calculate credit rate correction accordingly to client age.
     *
     * @param birthday clients birth-date.
     * @param gender   clients gender.
     * @return credit rate correction coefficient.
     * @throws ScoringException if client age is out of acceptable range.
     */
    private double getAgeRateCorrection(LocalDate birthday, ModelsScoringDataDTO.GenderEnum gender) throws ScoringException {

        int age = Period.between(birthday, LocalDate.now()).getYears();
        if (logger.isTraceEnabled()) logger.trace("Customers birthday is {}. Calculated age: {}.", birthday, age);

        // Возраст менее 20 или более 60 лет → отказ
        if (age < 20 || age > 60) {

            throw new ScoringException(ScoringException.ExceptionCause.UNACCEPTABLE_AGE);

        } else if (age >= 30 && age <= 55 && gender.equals(ModelsScoringDataDTO.GenderEnum.MALE)) {
            // Мужчина, возраст от 30 до 55 лет → ставка уменьшается на 3

            if (logger.isTraceEnabled())
                logger.trace("Credit rate is decreased by 3 because male with fine age.");
            return -3;

        } else if (age >= 35 && age <= 55 && gender.equals(ModelsScoringDataDTO.GenderEnum.FEMALE)) {
            // Женщина, возраст от 35 до 60 лет → ставка уменьшается на 3

            if (logger.isTraceEnabled())
                logger.trace("Credit rate is decreased by 3 because female with fine age.");
            return -3;

        } else if (gender.equals(ModelsScoringDataDTO.GenderEnum.NON_BINARY)) {
            // Небинарный → ставка увеличивается на 3

            if (logger.isTraceEnabled()) logger.trace("Credit rate is increased by 3 because non-binary person.");
            return 3;

        } else return 0;

    }

    /**
     * Calculate credit rate correction accordingly to client work experience.
     *
     * @param totalExperience   clients total work experience.
     * @param currentExperience clients work experience in current position.
     * @return credit rate correction coefficient.
     * @throws ScoringException if client work experience is insufficient.
     */
    private double getExperienceRateCorrection(Integer totalExperience, Integer currentExperience) throws ScoringException {
        // Стаж работы:
        // Общий стаж менее 12 месяцев → отказ
        // Текущий стаж менее 3 месяцев → отказ
        if (totalExperience < 12 || currentExperience < 3) {
            throw new ScoringException(ScoringException.ExceptionCause.INSUFFICIENT_EXPERIENCE);
        }

        return 0;
    }

}
