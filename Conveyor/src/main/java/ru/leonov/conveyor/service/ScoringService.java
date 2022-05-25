package ru.leonov.conveyor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.leonov.conveyor.dto.EmploymentDTO;
import ru.leonov.conveyor.dto.ScoringDataDTO;
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

    private final BigDecimal salaryToLoanRateLimit;
    private final BigDecimal baseRate;

    @SuppressWarnings("unused")
    @Value("${app-params.scoring.minTotalExperience}")
    private Integer minTotalExperience;
    @SuppressWarnings("unused")
    @Value("${app-params.scoring.minCurrentExperience}")
    private Integer minCurrentExperience;
    @SuppressWarnings("unused")
    @Value("${app-params.scoring.minLoanAge}")
    private Integer minLoanAge;
    @SuppressWarnings("unused")
    @Value("${app-params.scoring.maxLoanAge}")
    private Integer maxLoanAge;
    @SuppressWarnings("unused")
    @Value("${app-params.scoring.malePreferredAgeMin}")
    private Integer malePreferredAgeMin;
    @SuppressWarnings("unused")
    @Value("${app-params.scoring.malePreferredAgeMax}")
    private Integer malePreferredAgeMax;
    @SuppressWarnings("unused")
    @Value("${app-params.scoring.malePreferredAgeCorrection}")
    private Double malePreferredAgeCorrection;
    @SuppressWarnings("unused")
    @Value("${app-params.scoring.femalePreferredAgeMin}")
    private Integer femalePreferredAgeMin;
    @SuppressWarnings("unused")
    @Value("${app-params.scoring.femalePreferredAgeMax}")
    private Integer femalePreferredAgeMax;
    @SuppressWarnings("unused")
    @Value("${app-params.scoring.femalePreferredAgeCorrection}")
    private Double femalePreferredAgeCorrection;
    @SuppressWarnings("unused")
    @Value("${app-params.scoring.preferredDependentAmountMax}")
    private Integer preferredDependentAmountMax;
    @SuppressWarnings("unused")
    @Value("${app-params.scoring.dependentAmountCorrection}")
    private Double dependentAmountCorrection;
    @SuppressWarnings("unused")
    @Value("${app-params.scoring.selfEmployedCorrection}")
    private Double selfEmployedCorrection;
    @SuppressWarnings("unused")
    @Value("${app-params.scoring.businessOwnerCorrection}")
    private Double businessOwnerCorrection;
    @SuppressWarnings("unused")
    @Value("${app-params.scoring.midManagerCorrection}")
    private Double midManagerCorrection;
    @SuppressWarnings("unused")
    @Value("${app-params.scoring.topManagerCorrection}")
    private Double topManagerCorrection;
    @SuppressWarnings("unused")
    @Value("${app-params.scoring.marriedCorrection}")
    private Double marriedCorrection;
    @SuppressWarnings("unused")
    @Value("${app-params.scoring.divorcedCorrection}")
    private Double divorcedCorrection;
    @SuppressWarnings("unused")
    @Value("${app-params.scoring.nonBinaryCorrection}")
    private Double nonBinaryCorrection;

    @Autowired
    public ScoringService(@Value("${app-params.baseRate}") Double baseRate,
                          @Value("${app-params.scoring.salaryToLoanRateLimit}") Double salaryToLoanRateLimit) {
        this.baseRate = BigDecimal.valueOf(baseRate);
        this.salaryToLoanRateLimit = BigDecimal.valueOf(salaryToLoanRateLimit);
    }

    /**
     * Calculate credit rate to credit request.
     *
     * @param scoringData data to perform scoring.
     * @return credit rate.
     * @throws ScoringException if scoring data is unacceptable to get credit.
     */
    public BigDecimal calculateRate(ScoringDataDTO scoringData) throws ScoringException {

        log.trace("Calculating credit rate. Base rate is {}.", baseRate);

        BigDecimal resultRate = baseRate;

        resultRate = resultRate.add(getJobCorrection(scoringData.getEmployment().getEmploymentStatus(),
                scoringData.getEmployment().getPosition()));
        resultRate = resultRate.add(getSalaryCorrection(scoringData.getAmount(), scoringData.getEmployment().getSalary()));
        resultRate = resultRate.add(getFamilyCorrection(scoringData.getMaritalStatus(), scoringData.getDependentAmount()));
        resultRate = resultRate.add(getAgeRateCorrection(scoringData.getBirthdate(), scoringData.getGender()));
        resultRate = resultRate.add(getExperienceRateCorrection(
                scoringData.getEmployment().getWorkExperienceTotal(),
                scoringData.getEmployment().getWorkExperienceCurrent()));

        log.debug("Credit rate is calculated: {}.", resultRate);

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
    private BigDecimal getJobCorrection(EmploymentDTO.EmploymentStatusEnum employmentStatus,
                                        EmploymentDTO.PositionEnum jobPosition) throws ScoringException {

        BigDecimal resultCorrection = BigDecimal.ZERO;

        if (employmentStatus.equals(EmploymentDTO.EmploymentStatusEnum.UNEMPLOYED)) {
            // Безработный → отказ
            throw new ScoringException(ScoringException.ExceptionCause.UNACCEPTABLE_EMPLOYER_STATUS);
        } else if (employmentStatus.equals(EmploymentDTO.EmploymentStatusEnum.SELF_EMPLOYED)) {
            // Самозанятый → ставка увеличивается на 1
            resultCorrection = resultCorrection.add(BigDecimal.valueOf(selfEmployedCorrection));
            log.trace("Credit rate is increased by 1 because self-employed.");
        } else if (employmentStatus.equals(EmploymentDTO.EmploymentStatusEnum.BUSINESS_OWNER)) {
            // Владелец бизнеса → ставка увеличивается на 3
            resultCorrection = resultCorrection.add(BigDecimal.valueOf(businessOwnerCorrection));
            log.trace("Credit rate is increased by 3 because business owner.");
        }

        if (jobPosition.equals(EmploymentDTO.PositionEnum.MID_MANAGER)) {
            // Менеджер среднего звена → ставка уменьшается на 2
            resultCorrection = resultCorrection.add(BigDecimal.valueOf(midManagerCorrection));
            log.trace("Credit rate is decreased by 2 because mid-manager.");

        } else if (jobPosition.equals(EmploymentDTO.PositionEnum.TOP_MANAGER)) {
            // Топ-менеджер → ставка уменьшается на 4
            resultCorrection = resultCorrection.add(BigDecimal.valueOf(topManagerCorrection));
            log.trace("Credit rate is decreased by 4 because top-manager.");
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
        if (creditAmount.compareTo(salary.multiply(salaryToLoanRateLimit)) > 0) {
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
    private BigDecimal getFamilyCorrection(ScoringDataDTO.MaritalStatusEnum maritalStatus, Integer dependentAmount) {

        BigDecimal resultCorrection = BigDecimal.ZERO;

        if (maritalStatus.equals(ScoringDataDTO.MaritalStatusEnum.MARRIED)) {
            // Замужем/женат → ставка уменьшается на 3
            log.trace("Credit rate is decreased by 3 because married.");
            resultCorrection = resultCorrection.add(BigDecimal.valueOf(marriedCorrection));
        } else if (maritalStatus.equals(ScoringDataDTO.MaritalStatusEnum.DIVORCED)) {
            // Разведен → ставка увеличивается на 1
            log.trace("Credit rate is increased by 1 because divorced.");
            resultCorrection = resultCorrection.add(BigDecimal.valueOf(divorcedCorrection));
        }

        if (dependentAmount > preferredDependentAmountMax) {
            //Количество иждивенцев больше 1 → ставка увеличивается на 1
            resultCorrection = resultCorrection.add(BigDecimal.valueOf(dependentAmountCorrection));
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
    private BigDecimal getAgeRateCorrection(LocalDate birthday, ScoringDataDTO.GenderEnum gender) throws ScoringException {

        BigDecimal resultCorrection = BigDecimal.ZERO;

        int age = Period.between(birthday, LocalDate.now()).getYears();
        log.trace("Customers birthday is {}. Calculated age: {}.", birthday, age);

        // Возраст менее 20 или более 60 лет → отказ
        if (age < minLoanAge || age > maxLoanAge) {

            throw new ScoringException(ScoringException.ExceptionCause.UNACCEPTABLE_AGE);

        } else if (gender.equals(ScoringDataDTO.GenderEnum.MALE)
                && age >= malePreferredAgeMin && age <= malePreferredAgeMax) {
            // Мужчина, возраст от 30 до 55 лет → ставка уменьшается на 3

            log.trace("Credit rate is decreased by 3 because male with fine age.");
            resultCorrection = resultCorrection.add(BigDecimal.valueOf(malePreferredAgeCorrection));

        } else if (gender.equals(ScoringDataDTO.GenderEnum.FEMALE)
                && age >= femalePreferredAgeMin && age <= femalePreferredAgeMax) {
            // Женщина, возраст от 35 до 60 лет → ставка уменьшается на 3

            log.trace("Credit rate is decreased by 3 because female with fine age.");
            resultCorrection = resultCorrection.add(BigDecimal.valueOf(femalePreferredAgeCorrection));

        } else if (gender.equals(ScoringDataDTO.GenderEnum.NON_BINARY)) {
            // Небинарный → ставка увеличивается на 3

            log.trace("Credit rate is increased by 3 because non-binary person.");
            resultCorrection = resultCorrection.add(BigDecimal.valueOf(nonBinaryCorrection));

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
        if (totalExperience < minTotalExperience || currentExperience < minCurrentExperience) {
            throw new ScoringException(ScoringException.ExceptionCause.INSUFFICIENT_EXPERIENCE);
        }

        return BigDecimal.ZERO;
    }

}
