package ru.leonov.conveyor.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import ru.leonov.conveyor.dto.EmploymentDTO;
import ru.leonov.conveyor.dto.ScoringDataDTO;
import ru.leonov.conveyor.exceptions.ScoringException;
import ru.leonov.conveyor.test_data.LoanCalculationTestData;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ScoringServiceTest {

    // Общий стаж менее 12 месяцев → отказ
    @Value("${app-params.scoring.minTotalExperience}")
    Integer MIN_TOTAL_EXPERIENCE;

    // Текущий стаж менее 3 месяцев → отказ
    @Value("${app-params.scoring.minCurrentExperience}")
    Integer MIN_CURRENT_EXPERIENCE;

    // Возраст менее 20 или более 60 лет → отказ
    @Value("${app-params.scoring.minLoanAge}")
    Integer MIN_LOAN_AGE;
    @Value("${app-params.scoring.maxLoanAge}")
    Integer MAX_LOAN_AGE;

    // Мужчина, возраст от 30 до 55 лет → ставка уменьшается на 3
    @Value("${app-params.scoring.malePreferredAgeMin}")
    Integer MALE_PREFERRED_AGE_MIN;
    @Value("${app-params.scoring.malePreferredAgeMax}")
    Integer MALE_PREFERRED_AGE_MAX;
    @Value("${app-params.scoring.malePreferredAgeCorrection}")
    double MALE_PREFERRED_AGE_CORRECTION;

    // Женщина, возраст от 35 до 60 лет → ставка уменьшается на 3
    @Value("${app-params.scoring.femalePreferredAgeMin}")
    Integer FEMALE_PREFERRED_AGE_MIN;
    @Value("${app-params.scoring.femalePreferredAgeMax}")
    Integer FEMALE_PREFERRED_AGE_MAX;
    @Value("${app-params.scoring.femalePreferredAgeCorrection}")
    double FEMALE_PREFERRED_AGE_CORRECTION;

    //Количество иждивенцев больше 1 → ставка увеличивается на 1
    @Value("${app-params.scoring.preferredDependentAmountMax}")
    Integer PREFERRED_DEPENDENT_AMOUNT_MAX;
    @Value("${app-params.scoring.dependentAmountCorrection}")
    Integer DEPENDENT_AMOUNT_CORRECTION;

    //Сумма займа больше, чем 20 зарплат → отказ
    @Value("${app-params.scoring.salaryToLoanRateLimit}")
    double SALARY_TO_LOAN_RATE_LIMIT;

    // Самозанятый → ставка увеличивается на 1
    @Value("${app-params.scoring.selfEmployedCorrection}")
    double SELF_EMPLOYED_CORRECTION;

    // Владелец бизнеса → ставка увеличивается на 3
    @Value("${app-params.scoring.businessOwnerCorrection}")
    double BUSINESS_OWNER_CORRECTION;

    // Менеджер среднего звена → ставка уменьшается на 2
    @Value("${app-params.scoring.midManagerCorrection}")
    double MID_MANAGER_CORRECTION;

    // Топ-менеджер → ставка уменьшается на 4
    @Value("${app-params.scoring.topManagerCorrection}")
    double TOP_MANAGER_CORRECTION;

    // Замужем/женат → ставка уменьшается на 3
    @Value("${app-params.scoring.marriedCorrection}")
    double MARRIED_CORRECTION;

    // Разведен → ставка увеличивается на 1
    @Value("${app-params.scoring.divorcedCorrection}")
    double DIVORCED_CORRECTION;

    // Небинарный → ставка увеличивается на 3
    @Value("${app-params.scoring.nonBinaryCorrection}")
    double NON_BINARY_CORRECTION;

    @Value("${app-params.baseRate}")
    double BASE_RATE;

    @SuppressWarnings("unused")
    @Autowired
    private ScoringService scoringService;

    private ScoringDataDTO baseRateScoringRequest = new ScoringDataDTO();

    @BeforeEach
    void SetUp() {

        //Before each of test, generating request that is expected to produce base credit rate after scoring.
        baseRateScoringRequest = LoanCalculationTestData.getFineLoanCalculationRequestObject();

        baseRateScoringRequest.setGender(ScoringDataDTO.GenderEnum.MALE);
        baseRateScoringRequest.setBirthdate(LocalDate.now().minusYears(MALE_PREFERRED_AGE_MIN - 1));
        baseRateScoringRequest.setMaritalStatus(ScoringDataDTO.MaritalStatusEnum.SINGLE);
        baseRateScoringRequest.setDependentAmount(0);

        EmploymentDTO employmentDTO = baseRateScoringRequest.getEmployment();
        employmentDTO.setEmploymentStatus(EmploymentDTO.EmploymentStatusEnum.EMPLOYED);
        employmentDTO.setSalary(baseRateScoringRequest.getAmount()
                .multiply(BigDecimal.valueOf(SALARY_TO_LOAN_RATE_LIMIT)
                        .subtract(BigDecimal.ONE)));
        employmentDTO.setPosition(EmploymentDTO.PositionEnum.WORKER);
        employmentDTO.setWorkExperienceCurrent(MIN_CURRENT_EXPERIENCE);
        employmentDTO.setWorkExperienceTotal(MIN_TOTAL_EXPERIENCE);

        baseRateScoringRequest.setEmployment(employmentDTO);
    }

    //testing not affected fine request. expecting that calculated rate is equal to base rate
    @Test
    void calculateCreditRate() throws ScoringException {

        BigDecimal expectedRate = BigDecimal.valueOf(BASE_RATE);
        BigDecimal calculatedRate = scoringService.calculateRate(baseRateScoringRequest);

        assertEquals(0, calculatedRate.compareTo(expectedRate));
    }

    // Мужчина, возраст от 30 до 55 лет → ставка уменьшается на 3
    @Test
    void calculateCreditRateMaleFineAge() throws ScoringException {
        //testing four cases to inspect all possible boundary conditions

        BigDecimal expectedRateWithinFineAge = BigDecimal.valueOf(BASE_RATE + MALE_PREFERRED_AGE_CORRECTION);
        BigDecimal expectedRateOutOfFineAge = BigDecimal.valueOf(BASE_RATE);

        baseRateScoringRequest.setGender(ScoringDataDTO.GenderEnum.MALE);

        BigDecimal calculatedRate;

        //---tests of male within fine age
        //testing minimum fine male age
        baseRateScoringRequest.setBirthdate(LocalDate.now().minusYears(MALE_PREFERRED_AGE_MIN));
        calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
        assertEquals(0, calculatedRate.compareTo(expectedRateWithinFineAge));

        //testing maximum fine male age
        baseRateScoringRequest.setBirthdate(LocalDate.now().minusYears(MALE_PREFERRED_AGE_MAX));
        calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
        assertEquals(0, calculatedRate.compareTo(expectedRateWithinFineAge));


        //---tests of male out of fine age
        //testing male age less than minimum
        baseRateScoringRequest.setBirthdate(LocalDate.now().minusYears(MALE_PREFERRED_AGE_MIN - 1));
        calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
        assertEquals(0, calculatedRate.compareTo(expectedRateOutOfFineAge));

        //testing male age more than maximum
        baseRateScoringRequest.setBirthdate(LocalDate.now().minusYears(MALE_PREFERRED_AGE_MAX + 1));
        calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
        assertEquals(0, calculatedRate.compareTo(expectedRateOutOfFineAge));

    }

    // Женщина, возраст от 35 до 60 лет → ставка уменьшается на 3
    @Test
    void calculateCreditRateFemaleFineAge() throws ScoringException {
        //testing four cases to inspect all possible boundary conditions

        baseRateScoringRequest.setGender(ScoringDataDTO.GenderEnum.FEMALE);

        BigDecimal expectedRateWithinFineAge = BigDecimal.valueOf(BASE_RATE + FEMALE_PREFERRED_AGE_CORRECTION);
        BigDecimal expectedRateOutOfFineAge = BigDecimal.valueOf(BASE_RATE);

        BigDecimal calculatedRate;

        //testing minimum fine female age
        baseRateScoringRequest.setBirthdate(LocalDate.now().minusYears(FEMALE_PREFERRED_AGE_MIN));
        calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
        assertEquals(0, calculatedRate.compareTo(expectedRateWithinFineAge));

        //testing maximum fine female age
        baseRateScoringRequest.setBirthdate(LocalDate.now().minusYears(FEMALE_PREFERRED_AGE_MAX));
        calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
        assertEquals(0, calculatedRate.compareTo(expectedRateWithinFineAge));


        //testing female age less than minimum
        baseRateScoringRequest.setBirthdate(LocalDate.now().minusYears(FEMALE_PREFERRED_AGE_MIN - 1));
        calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
        assertEquals(0, calculatedRate.compareTo(expectedRateOutOfFineAge));

        //testing female age more than maximum is impossible because it is out of MAX_LOAN_AGE boundary

    }

    // Количество иждивенцев больше 1 → ставка увеличивается на 1
    @Test
    void calculateCreditRateLotOfDependents() throws ScoringException {
        //testing two possible boundary conditions

        BigDecimal expectedRateWithFineDependentNumber = BigDecimal.valueOf(BASE_RATE);
        BigDecimal expectedRateWithTooMuchDependentNumber = BigDecimal.valueOf(BASE_RATE + DEPENDENT_AMOUNT_CORRECTION);
        BigDecimal calculatedRate;


        //testing fine dependent amount
        baseRateScoringRequest.setDependentAmount(PREFERRED_DEPENDENT_AMOUNT_MAX);
        calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
        assertEquals(0, calculatedRate.compareTo(expectedRateWithFineDependentNumber));

        //testing exceeding of fine dependent amount
        baseRateScoringRequest.setDependentAmount(PREFERRED_DEPENDENT_AMOUNT_MAX + 1);
        calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
        assertEquals(0, calculatedRate.compareTo(expectedRateWithTooMuchDependentNumber));

    }

    // Самозанятый → ставка увеличивается на 1
    @Test
    void calculateCreditRateSelfEmployed() throws ScoringException {

        BigDecimal expectedRate = BigDecimal.valueOf(BASE_RATE + SELF_EMPLOYED_CORRECTION);

        EmploymentDTO testEmployment = baseRateScoringRequest.getEmployment();
        testEmployment.setEmploymentStatus(EmploymentDTO.EmploymentStatusEnum.SELF_EMPLOYED);

        baseRateScoringRequest.setEmployment(testEmployment);

        BigDecimal calculatedRate = scoringService.calculateRate(baseRateScoringRequest);

        assertEquals(0, calculatedRate.compareTo(expectedRate));
    }

    // Владелец бизнеса → ставка увеличивается на 3
    @Test
    void calculateCreditRateBusinessOwner() throws ScoringException {

        BigDecimal expectedRate = BigDecimal.valueOf(BASE_RATE + BUSINESS_OWNER_CORRECTION);

        EmploymentDTO testEmployment = baseRateScoringRequest.getEmployment();
        testEmployment.setEmploymentStatus(EmploymentDTO.EmploymentStatusEnum.BUSINESS_OWNER);

        baseRateScoringRequest.setEmployment(testEmployment);

        BigDecimal calculatedRate = scoringService.calculateRate(baseRateScoringRequest);

        assertEquals(0, calculatedRate.compareTo(expectedRate));
    }

    // Менеджер среднего звена → ставка уменьшается на 2
    @Test
    void calculateCreditRateMidManager() throws ScoringException {

        BigDecimal expectedRate = BigDecimal.valueOf(BASE_RATE + MID_MANAGER_CORRECTION);

        EmploymentDTO testEmployment = baseRateScoringRequest.getEmployment();
        testEmployment.setPosition(EmploymentDTO.PositionEnum.MID_MANAGER);

        baseRateScoringRequest.setEmployment(testEmployment);

        BigDecimal calculatedRate = scoringService.calculateRate(baseRateScoringRequest);

        assertEquals(0, calculatedRate.compareTo(expectedRate));
    }

    // Топ-менеджер → ставка уменьшается на 4
    @Test
    void calculateCreditRateTopManager() throws ScoringException {

        BigDecimal expectedRate = BigDecimal.valueOf(BASE_RATE + TOP_MANAGER_CORRECTION);

        EmploymentDTO testEmployment = baseRateScoringRequest.getEmployment();
        testEmployment.setPosition(EmploymentDTO.PositionEnum.TOP_MANAGER);

        baseRateScoringRequest.setEmployment(testEmployment);

        BigDecimal calculatedRate = scoringService.calculateRate(baseRateScoringRequest);

        assertEquals(0, calculatedRate.compareTo(expectedRate));
    }

    // Замужем/женат → ставка уменьшается на 3
    @Test
    void calculateCreditRateMarried() throws ScoringException {

        BigDecimal expectedRate = BigDecimal.valueOf(BASE_RATE + MARRIED_CORRECTION);

        baseRateScoringRequest.setMaritalStatus(ScoringDataDTO.MaritalStatusEnum.MARRIED);

        BigDecimal calculatedRate = scoringService.calculateRate(baseRateScoringRequest);

        assertEquals(0, calculatedRate.compareTo(expectedRate));
    }

    // Разведен → ставка увеличивается на 1
    @Test
    void calculateCreditRateDivorced() throws ScoringException {

        BigDecimal expectedRate = BigDecimal.valueOf(BASE_RATE + DIVORCED_CORRECTION);

        baseRateScoringRequest.setMaritalStatus(ScoringDataDTO.MaritalStatusEnum.DIVORCED);

        BigDecimal calculatedRate = scoringService.calculateRate(baseRateScoringRequest);

        assertEquals(0, calculatedRate.compareTo(expectedRate));
    }

    // Небинарный → ставка увеличивается на 3
    @Test
    void calculateCreditRateNonBinary() throws ScoringException {

        BigDecimal expectedRate = BigDecimal.valueOf(BASE_RATE + NON_BINARY_CORRECTION);

        baseRateScoringRequest.setGender(ScoringDataDTO.GenderEnum.NON_BINARY);

        BigDecimal calculatedRate = scoringService.calculateRate(baseRateScoringRequest);

        assertEquals(0, calculatedRate.compareTo(expectedRate));
    }

    // Общий стаж менее 12 месяцев → отказ
    @Test
    void calculateCreditRateFewTotalExperience() {

        String expectedExceptionMessage = ScoringException.ExceptionCause.INSUFFICIENT_EXPERIENCE.getUserFriendlyMessage();

        EmploymentDTO employmentDTO = baseRateScoringRequest.getEmployment();
        employmentDTO.setWorkExperienceTotal(MIN_TOTAL_EXPERIENCE - 1);
        baseRateScoringRequest.setEmployment(employmentDTO);


        ScoringException receivedException = assertThrows(ScoringException.class,
                () -> scoringService.calculateRate(baseRateScoringRequest));

        assertEquals(expectedExceptionMessage, receivedException.getMessage());

    }

    // Текущий стаж менее 3 месяцев → отказ
    @Test
    void calculateCreditRateFewCurrentExperience() {

        String expectedExceptionMessage = ScoringException.ExceptionCause.INSUFFICIENT_EXPERIENCE.getUserFriendlyMessage();

        EmploymentDTO employmentDTO = baseRateScoringRequest.getEmployment();
        employmentDTO.setWorkExperienceTotal(MIN_CURRENT_EXPERIENCE - 1);
        baseRateScoringRequest.setEmployment(employmentDTO);

        ScoringException receivedException = assertThrows(ScoringException.class,
                () -> scoringService.calculateRate(baseRateScoringRequest));

        assertEquals(expectedExceptionMessage, receivedException.getMessage());

    }

    // Возраст менее 20 или более 60 лет → отказ
    @Test
    void calculateCreditRateAgeRefuse() throws ScoringException {

        String expectedExceptionMessage = ScoringException.ExceptionCause.UNACCEPTABLE_AGE.getUserFriendlyMessage();

        //checking that person with minimum possible age will pass credit scoring
        baseRateScoringRequest.setBirthdate(LocalDate.now().minusYears(MIN_LOAN_AGE));
        BigDecimal calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
        BigDecimal expectedRate = BigDecimal.valueOf(BASE_RATE);

        assertEquals(0, expectedRate.compareTo(calculatedRate));


        //checking that person with less than minimum possible age will not pass credit scoring
        baseRateScoringRequest.setBirthdate(LocalDate.now().minusYears(MIN_LOAN_AGE - 1));
        ScoringException receivedException = assertThrows(ScoringException.class,
                () -> scoringService.calculateRate(baseRateScoringRequest));

        assertEquals(expectedExceptionMessage, receivedException.getMessage());


        //checking that person with maximum possible age will pass credit scoring
        baseRateScoringRequest.setBirthdate(LocalDate.now().minusYears(MAX_LOAN_AGE));
        calculatedRate = scoringService.calculateRate(baseRateScoringRequest);

        assertEquals(0, expectedRate.compareTo(calculatedRate));


        //checking that person with more than maximum possible age will not pass credit scoring
        baseRateScoringRequest.setBirthdate(LocalDate.now().minusYears(MAX_LOAN_AGE + 1));
        receivedException = assertThrows(ScoringException.class,
                () -> scoringService.calculateRate(baseRateScoringRequest));

        assertEquals(expectedExceptionMessage, receivedException.getMessage());

    }

    // Сумма займа больше, чем 20 зарплат → отказ
    @Test
    void calculateCreditRateTooMuchAmountForSalary() throws ScoringException {

        String expectedExceptionMessage = ScoringException.ExceptionCause.INSUFFICIENT_SALARY.getUserFriendlyMessage();

        BigDecimal fineSalary = baseRateScoringRequest.getAmount()
                .divide(BigDecimal.valueOf(SALARY_TO_LOAN_RATE_LIMIT), MathContext.DECIMAL64);
        BigDecimal fewSalary = fineSalary.subtract(BigDecimal.ONE);

        EmploymentDTO employmentDTO = baseRateScoringRequest.getEmployment();

        //customer with fine salary should pass scoring
        employmentDTO.setSalary(fineSalary);
        baseRateScoringRequest.setEmployment(employmentDTO);
        BigDecimal calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
        BigDecimal expectedRate = BigDecimal.valueOf(BASE_RATE);

        assertEquals(0, expectedRate.compareTo(calculatedRate));


        //customer with too few salary shouldn't pass scoring
        employmentDTO.setSalary(fewSalary);
        baseRateScoringRequest.setEmployment(employmentDTO);
        ScoringException receivedException = assertThrows(ScoringException.class,
                () -> scoringService.calculateRate(baseRateScoringRequest));

        assertEquals(expectedExceptionMessage, receivedException.getMessage());
    }

    // Безработный → отказ
    @Test
    void calculateCreditRateUnemployed() {

        String expectedExceptionMessage = ScoringException.ExceptionCause.UNACCEPTABLE_EMPLOYER_STATUS.getUserFriendlyMessage();

        EmploymentDTO employmentDTO = baseRateScoringRequest.getEmployment();
        employmentDTO.setEmploymentStatus(EmploymentDTO.EmploymentStatusEnum.UNEMPLOYED);
        baseRateScoringRequest.setEmployment(employmentDTO);

        ScoringException receivedException = assertThrows(ScoringException.class,
                () -> scoringService.calculateRate(baseRateScoringRequest));

        assertEquals(expectedExceptionMessage, receivedException.getMessage());
    }

}