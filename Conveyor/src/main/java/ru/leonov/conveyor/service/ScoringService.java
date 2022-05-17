package ru.leonov.conveyor.service;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
public class ScoringService {

    // Общий стаж менее 12 месяцев → отказ
    // Текущий стаж менее 3 месяцев → отказ
    private static final Integer MIN_TOTAL_EXPERIENCE = 12;
    private static final Integer MIN_CURRENT_EXPERIENCE = 3;

    // Возраст менее 20 или более 60 лет → отказ
    private static final Integer MIN_LOAN_AGE = 20;
    private static final Integer MAX_LOAN_AGE = 60;

    // Мужчина, возраст от 30 до 55 лет → ставка уменьшается на 3
    // Женщина, возраст от 35 до 60 лет → ставка уменьшается на 3
    private static final Integer MALE_PREFERRED_AGE_MIN = 30;
    private static final Integer MALE_PREFERRED_AGE_MAX = 55;
    private static final Integer FEMALE_PREFERRED_AGE_MIN = 35;
    private static final Integer FEMALE_PREFERRED_AGE_MAX = 60;

    //Количество иждивенцев больше 1 → ставка увеличивается на 1
    private static final Integer PREFERRED_DEPENDENT_AMOUNT_MAX = 1;

    //Сумма займа больше, чем 20 зарплат → отказ
    private static final BigDecimal SALARY_TO_LOAN_RATE_LIMIT = BigDecimal.valueOf(20);

    private final BigDecimal baseRate;
    private final CreditCalculationService creditCalculationService;

    @Autowired
    public ScoringService(@Value("${app-params.baseRate}") double baseRate,
                          CreditCalculationService creditCalculationService) {
        this.baseRate = BigDecimal.valueOf(baseRate);
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

        BigDecimal rate = calculateRate(scoringData);

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
    private BigDecimal calculateRate(ModelsScoringDataDTO scoringData) throws ScoringException {

        if (log.isTraceEnabled()) log.trace("Calculating credit rate. Base rate is {}.", baseRate);

        BigDecimal resultRate = baseRate;

        resultRate = resultRate.add(getJobCorrection(scoringData.getEmployment().getEmploymentStatus(),
                scoringData.getEmployment().getPosition()));
        resultRate = resultRate.add(getSalaryCorrection(scoringData.getAmount(), scoringData.getEmployment().getSalary()));
        resultRate = resultRate.add(getFamilyCorrection(scoringData.getMaritalStatus(), scoringData.getDependentAmount()));
        resultRate = resultRate.add(getAgeRateCorrection(scoringData.getBirthdate(), scoringData.getGender()));
        resultRate = resultRate.add(getExperienceRateCorrection(
                scoringData.getEmployment().getWorkExperienceTotal(),
                scoringData.getEmployment().getWorkExperienceCurrent()));

        if (log.isDebugEnabled()) log.debug("Credit rate is calculated: {}.", resultRate);

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
    private BigDecimal getJobCorrection(ModelsEmploymentDTO.EmploymentStatusEnum employmentStatus,
                                        ModelsEmploymentDTO.PositionEnum jobPosition) throws ScoringException {

        BigDecimal resultCorrection = BigDecimal.ZERO;

        if (employmentStatus.equals(ModelsEmploymentDTO.EmploymentStatusEnum.UNEMPLOYED)) {
            // Безработный → отказ
            throw new ScoringException(ScoringException.ExceptionCause.UNACCEPTABLE_EMPLOYER_STATUS);
        } else if (employmentStatus.equals(ModelsEmploymentDTO.EmploymentStatusEnum.SELF_EMPLOYED)) {
            // Самозанятый → ставка увеличивается на 1
            resultCorrection = resultCorrection.add(BigDecimal.ONE);
            if (log.isTraceEnabled()) log.trace("Credit rate is increased by 1 because self-employed.");
        } else if (employmentStatus.equals(ModelsEmploymentDTO.EmploymentStatusEnum.BUSINESS_OWNER)) {
            // Владелец бизнеса → ставка увеличивается на 3
            resultCorrection = resultCorrection.add(BigDecimal.valueOf(3));
            if (log.isTraceEnabled()) log.trace("Credit rate is increased by 3 because business owner.");
        }

        if (jobPosition.equals(ModelsEmploymentDTO.PositionEnum.MID_MANAGER)) {
            // Менеджер среднего звена → ставка уменьшается на 2
            resultCorrection = resultCorrection.subtract(BigDecimal.valueOf(2));
            if (log.isTraceEnabled()) log.trace("Credit rate is decreased by 2 because mid-manager.");

        } else if (jobPosition.equals(ModelsEmploymentDTO.PositionEnum.TOP_MANAGER)) {
            // Топ-менеджер → ставка уменьшается на 4
            resultCorrection = resultCorrection.subtract(BigDecimal.valueOf(4));
            if (log.isTraceEnabled()) log.trace("Credit rate is decreased by 4 because top-manager.");
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
    private BigDecimal getSalaryCorrection(BigDecimal creditAmount, BigDecimal salary) throws ScoringException {
        if (creditAmount.compareTo(salary.multiply(SALARY_TO_LOAN_RATE_LIMIT)) > 0) {
            //Сумма займа больше, чем 20 зарплат → отказ
            throw new ScoringException(ScoringException.ExceptionCause.INSUFFICIENT_SALARY);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Calculate credit rate correction accordingly to client marital status.
     *
     * @param maritalStatus   martial status of customer.
     * @param dependentAmount amount of dependent persons (children etc.).
     * @return credit rate correction coefficient.
     */
    private BigDecimal getFamilyCorrection(ModelsScoringDataDTO.MaritalStatusEnum maritalStatus, Integer dependentAmount) {

        BigDecimal resultCorrection = BigDecimal.ZERO;

        if (maritalStatus.equals(ModelsScoringDataDTO.MaritalStatusEnum.MARRIED)) {
            // Замужем/женат → ставка уменьшается на 3
            if (log.isTraceEnabled()) log.trace("Credit rate is decreased by 3 because married.");
            resultCorrection = resultCorrection.subtract(BigDecimal.valueOf(3));
        } else if (maritalStatus.equals(ModelsScoringDataDTO.MaritalStatusEnum.DIVORCED)) {
            // Разведен → ставка увеличивается на 1
            if (log.isTraceEnabled()) log.trace("Credit rate is increased by 1 because divorced.");
            resultCorrection = resultCorrection.add(BigDecimal.ONE);
        }

        if (dependentAmount > PREFERRED_DEPENDENT_AMOUNT_MAX) {
            //Количество иждивенцев больше 1 → ставка увеличивается на 1
            resultCorrection = resultCorrection.add(BigDecimal.ONE);
            if (log.isTraceEnabled())
                log.trace("Credit rate is increased by 1 because too much dependent persons.");
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
    private BigDecimal getAgeRateCorrection(LocalDate birthday, ModelsScoringDataDTO.GenderEnum gender) throws ScoringException {

        BigDecimal resultCorrection = BigDecimal.ZERO;

        int age = Period.between(birthday, LocalDate.now()).getYears();
        if (log.isTraceEnabled()) log.trace("Customers birthday is {}. Calculated age: {}.", birthday, age);

        // Возраст менее 20 или более 60 лет → отказ
        if (age < MIN_LOAN_AGE || age > MAX_LOAN_AGE) {

            throw new ScoringException(ScoringException.ExceptionCause.UNACCEPTABLE_AGE);

        } else if (gender.equals(ModelsScoringDataDTO.GenderEnum.MALE)
                && age >= MALE_PREFERRED_AGE_MIN && age <= MALE_PREFERRED_AGE_MAX) {
            // Мужчина, возраст от 30 до 55 лет → ставка уменьшается на 3

            if (log.isTraceEnabled())
                log.trace("Credit rate is decreased by 3 because male with fine age.");
            resultCorrection = resultCorrection.subtract(BigDecimal.valueOf(3));

        } else if (gender.equals(ModelsScoringDataDTO.GenderEnum.FEMALE)
                && age >= FEMALE_PREFERRED_AGE_MIN && age <= FEMALE_PREFERRED_AGE_MAX) {
            // Женщина, возраст от 35 до 60 лет → ставка уменьшается на 3

            if (log.isTraceEnabled())
                log.trace("Credit rate is decreased by 3 because female with fine age.");
            resultCorrection = resultCorrection.subtract(BigDecimal.valueOf(3));

        } else if (gender.equals(ModelsScoringDataDTO.GenderEnum.NON_BINARY)) {
            // Небинарный → ставка увеличивается на 3

            if (log.isTraceEnabled()) log.trace("Credit rate is increased by 3 because non-binary person.");
            resultCorrection = resultCorrection.add(BigDecimal.valueOf(3));

        }

        return resultCorrection;
    }

    /**
     * Calculate credit rate correction accordingly to client work experience.
     *
     * @param totalExperience   clients total work experience.
     * @param currentExperience clients work experience in current position.
     * @return credit rate correction coefficient.
     * @throws ScoringException if client work experience is insufficient.
     */
    private BigDecimal getExperienceRateCorrection(Integer totalExperience, Integer currentExperience) throws ScoringException {
        // Стаж работы:
        // Общий стаж менее 12 месяцев → отказ
        // Текущий стаж менее 3 месяцев → отказ
        if (totalExperience < MIN_TOTAL_EXPERIENCE || currentExperience < MIN_CURRENT_EXPERIENCE) {
            throw new ScoringException(ScoringException.ExceptionCause.INSUFFICIENT_EXPERIENCE);
        }

        return BigDecimal.ZERO;
    }

}
