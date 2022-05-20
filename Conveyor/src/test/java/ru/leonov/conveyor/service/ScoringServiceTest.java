package ru.leonov.conveyor.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.leonov.conveyor.dto.EmploymentDTO;
import ru.leonov.conveyor.dto.ScoringDataDTO;
import ru.leonov.conveyor.exceptions.ScoringException;
import ru.leonov.conveyor.test_data.LoanCalculationTestData;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class ScoringServiceTest {

    // Общий стаж менее 12 месяцев → отказ
    // Текущий стаж менее 3 месяцев → отказ
    private static final Integer MIN_TOTAL_EXPERIENCE = 12;
    private static final Integer MIN_CURRENT_EXPERIENCE = 3;

    // Возраст менее 20 или более 60 лет → отказ
    private static final Integer MIN_LOAN_AGE = 20;
    private static final Integer MAX_LOAN_AGE = 60;

    // Мужчина, возраст от 30 до 55 лет → ставка уменьшается на 3
    private static final Integer MALE_PREFERRED_AGE_MIN = 30;
    private static final Integer MALE_PREFERRED_AGE_MAX = 55;
    private static final double MALE_PREFERRED_AGE_CORRECTION = -3;

    // Женщина, возраст от 35 до 60 лет → ставка уменьшается на 3
    private static final Integer FEMALE_PREFERRED_AGE_MIN = 35;
    private static final Integer FEMALE_PREFERRED_AGE_MAX = 60;
    private static final double FEMALE_PREFERRED_AGE_CORRECTION = -3;

    //Количество иждивенцев больше 1 → ставка увеличивается на 1
    private static final Integer PREFERRED_DEPENDENT_AMOUNT_MAX = 1;
    private static final Integer DEPENDENT_AMOUNT_CORRECTION = 1;

    //Сумма займа больше, чем 20 зарплат → отказ
    private static final BigDecimal SALARY_TO_LOAN_RATE_LIMIT = BigDecimal.valueOf(20);

    // Самозанятый → ставка увеличивается на 1
    private static final double SELF_EMPLOYED_CORRECTION = 1;

    // Владелец бизнеса → ставка увеличивается на 3
    private static final double BUSINESS_OWNER_CORRECTION = 3;

    // Менеджер среднего звена → ставка уменьшается на 2
    private static final double MID_MANAGER_CORRECTION = -2;

    // Топ-менеджер → ставка уменьшается на 4
    private static final double TOP_MANAGER_CORRECTION = -4;

    // Замужем/женат → ставка уменьшается на 3
    private static final double MARRIED_CORRECTION = -3;

    // Разведен → ставка увеличивается на 1
    private static final double DIVORCED_CORRECTION = 1;

    // Небинарный → ставка увеличивается на 3
    private static final double NON_BINARY_CORRECTION = 3;

    // Сумма займа больше, чем 20 зарплат → отказ
    private static final BigDecimal SALARY_TO_LOAN_RATE_MAX = BigDecimal.valueOf(20);


    private static final double BASE_RATE = 15;
    private ScoringService scoringService;
    private ScoringDataDTO baseRateScoringRequest = new ScoringDataDTO();

    @BeforeEach
    void SetUp() {
        scoringService = new ScoringService(BASE_RATE);

        baseRateScoringRequest = LoanCalculationTestData.getFineLoanCalculationRequestObject();

        baseRateScoringRequest.setGender(ScoringDataDTO.GenderEnum.MALE);
        baseRateScoringRequest.setBirthdate(LocalDate.now().minusYears(MALE_PREFERRED_AGE_MIN - 1));
        baseRateScoringRequest.setMaritalStatus(ScoringDataDTO.MaritalStatusEnum.SINGLE);
        baseRateScoringRequest.setDependentAmount(0);

        EmploymentDTO employmentDTO = baseRateScoringRequest.getEmployment();
        employmentDTO.setEmploymentStatus(EmploymentDTO.EmploymentStatusEnum.EMPLOYED);
        employmentDTO.setSalary(baseRateScoringRequest.getAmount().multiply(SALARY_TO_LOAN_RATE_LIMIT.subtract(BigDecimal.ONE)));
        employmentDTO.setPosition(EmploymentDTO.PositionEnum.WORKER);
        employmentDTO.setWorkExperienceCurrent(MIN_CURRENT_EXPERIENCE);
        employmentDTO.setWorkExperienceTotal(MIN_TOTAL_EXPERIENCE);

        baseRateScoringRequest.setEmployment(employmentDTO);
    }

    //testing not affected fine request. expecting that calculated rate is equal to base rate
    @Test
    void calculateCreditRate() {

        BigDecimal expectedRate = BigDecimal.valueOf(BASE_RATE);
        BigDecimal calculatedRate;

        try {
            calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
        } catch (ScoringException e) {
            throw new IllegalArgumentException("Got unexpected scoring exception!");
        }

        assertEquals(0, calculatedRate.compareTo(expectedRate));
    }

    // Мужчина, возраст от 30 до 55 лет → ставка уменьшается на 3
    @Test
    void calculateCreditRateMaleFineAge() {
        //testing four cases to inspect all possible boundary conditions

        BigDecimal expectedRate = BigDecimal.valueOf(BASE_RATE + MALE_PREFERRED_AGE_CORRECTION);

        baseRateScoringRequest.setGender(ScoringDataDTO.GenderEnum.MALE);

        BigDecimal calculatedRate;

        try {

            //testing minimum fine male age
            baseRateScoringRequest.setBirthdate(LocalDate.now().minusYears(MALE_PREFERRED_AGE_MIN));
            calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
            assertEquals(0, calculatedRate.compareTo(expectedRate));

            //testing maximum fine male age
            baseRateScoringRequest.setBirthdate(LocalDate.now().minusYears(MALE_PREFERRED_AGE_MAX));
            calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
            assertEquals(0, calculatedRate.compareTo(expectedRate));

        } catch (ScoringException e) {
            throw new IllegalArgumentException("Got unexpected scoring exception!");
        }


        expectedRate = BigDecimal.valueOf(BASE_RATE);

        try {
            //testing male age less than minimum
            baseRateScoringRequest.setBirthdate(LocalDate.now().minusYears(MALE_PREFERRED_AGE_MIN - 1));
            calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
            assertEquals(0, calculatedRate.compareTo(expectedRate));

            //testing male age more than maximum
            baseRateScoringRequest.setBirthdate(LocalDate.now().minusYears(MALE_PREFERRED_AGE_MAX + 1));
            calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
            assertEquals(0, calculatedRate.compareTo(expectedRate));

        } catch (ScoringException e) {
            throw new IllegalArgumentException("Got unexpected scoring exception!");
        }
    }

    // Женщина, возраст от 35 до 60 лет → ставка уменьшается на 3
    @Test
    void calculateCreditRateFemaleFineAge() {
        //testing four cases to inspect all possible boundary conditions

        baseRateScoringRequest.setGender(ScoringDataDTO.GenderEnum.FEMALE);
        BigDecimal expectedRate = BigDecimal.valueOf(BASE_RATE + FEMALE_PREFERRED_AGE_CORRECTION);
        BigDecimal calculatedRate;

        try {

            //testing minimum fine female age
            baseRateScoringRequest.setBirthdate(LocalDate.now().minusYears(FEMALE_PREFERRED_AGE_MIN));
            calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
            assertEquals(0, calculatedRate.compareTo(expectedRate));

            //testing maximum fine female age
            baseRateScoringRequest.setBirthdate(LocalDate.now().minusYears(FEMALE_PREFERRED_AGE_MAX));
            calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
            assertEquals(0, calculatedRate.compareTo(expectedRate));

        } catch (ScoringException e) {
            throw new IllegalArgumentException("Got unexpected scoring exception!");
        }

        expectedRate = BigDecimal.valueOf(BASE_RATE);

        try {
            //testing female age less than minimum
            baseRateScoringRequest.setBirthdate(LocalDate.now().minusYears(FEMALE_PREFERRED_AGE_MIN - 1));
            calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
            assertEquals(0, calculatedRate.compareTo(expectedRate));

            //testing female age more than maximum is impossible because it is out of MAX_LOAN_AGE boundary

        } catch (ScoringException e) {
            throw new IllegalArgumentException("Got unexpected scoring exception!");
        }
    }

    // Количество иждивенцев больше 1 → ставка увеличивается на 1
    @Test
    void calculateCreditRateLotOfDependents() {
        //testing two possible boundary conditions

        try {

            //testing fine dependent amount
            BigDecimal expectedRate = BigDecimal.valueOf(BASE_RATE);
            BigDecimal calculatedRate;
            baseRateScoringRequest.setDependentAmount(PREFERRED_DEPENDENT_AMOUNT_MAX);
            calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
            assertEquals(0, calculatedRate.compareTo(expectedRate));

            //testing exceeding of fine dependent amount
            expectedRate = BigDecimal.valueOf(BASE_RATE + DEPENDENT_AMOUNT_CORRECTION);
            baseRateScoringRequest.setDependentAmount(PREFERRED_DEPENDENT_AMOUNT_MAX + 1);
            calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
            assertEquals(0, calculatedRate.compareTo(expectedRate));

        } catch (ScoringException e) {
            throw new IllegalArgumentException("Got unexpected scoring exception!");
        }

    }

    // Самозанятый → ставка увеличивается на 1
    @Test
    void calculateCreditRateSelfEmployed() {

        BigDecimal expectedRate = BigDecimal.valueOf(BASE_RATE + SELF_EMPLOYED_CORRECTION);
        BigDecimal calculatedRate;

        EmploymentDTO testEmployment = baseRateScoringRequest.getEmployment();
        testEmployment.setEmploymentStatus(EmploymentDTO.EmploymentStatusEnum.SELF_EMPLOYED);

        baseRateScoringRequest.setEmployment(testEmployment);

        try {
            calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
        } catch (ScoringException e) {
            throw new IllegalArgumentException("Got unexpected scoring exception!");
        }

        assertEquals(0, calculatedRate.compareTo(expectedRate));
    }

    // Владелец бизнеса → ставка увеличивается на 3
    @Test
    void calculateCreditRateBusinessOwner() {

        BigDecimal expectedRate = BigDecimal.valueOf(BASE_RATE + BUSINESS_OWNER_CORRECTION);
        BigDecimal calculatedRate;

        EmploymentDTO testEmployment = baseRateScoringRequest.getEmployment();
        testEmployment.setEmploymentStatus(EmploymentDTO.EmploymentStatusEnum.BUSINESS_OWNER);

        baseRateScoringRequest.setEmployment(testEmployment);

        try {
            calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
        } catch (ScoringException e) {
            throw new IllegalArgumentException("Got unexpected scoring exception!");
        }

        assertEquals(0, calculatedRate.compareTo(expectedRate));
    }

    // Менеджер среднего звена → ставка уменьшается на 2
    @Test
    void calculateCreditRateMidManager() {

        BigDecimal expectedRate = BigDecimal.valueOf(BASE_RATE + MID_MANAGER_CORRECTION);
        BigDecimal calculatedRate;

        EmploymentDTO testEmployment = baseRateScoringRequest.getEmployment();
        testEmployment.setPosition(EmploymentDTO.PositionEnum.MID_MANAGER);

        baseRateScoringRequest.setEmployment(testEmployment);

        try {
            calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
        } catch (ScoringException e) {
            throw new IllegalArgumentException("Got unexpected scoring exception!");
        }

        assertEquals(0, calculatedRate.compareTo(expectedRate));
    }

    // Топ-менеджер → ставка уменьшается на 4
    @Test
    void calculateCreditRateTopManager() {

        BigDecimal expectedRate = BigDecimal.valueOf(BASE_RATE + TOP_MANAGER_CORRECTION);
        BigDecimal calculatedRate;

        EmploymentDTO testEmployment = baseRateScoringRequest.getEmployment();
        testEmployment.setPosition(EmploymentDTO.PositionEnum.TOP_MANAGER);

        baseRateScoringRequest.setEmployment(testEmployment);

        try {
            calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
        } catch (ScoringException e) {
            throw new IllegalArgumentException("Got unexpected scoring exception!");
        }

        assertEquals(0, calculatedRate.compareTo(expectedRate));
    }

    // Замужем/женат → ставка уменьшается на 3
    @Test
    void calculateCreditRateMarried() {

        BigDecimal expectedRate = BigDecimal.valueOf(BASE_RATE + MARRIED_CORRECTION);
        BigDecimal calculatedRate;

        baseRateScoringRequest.setMaritalStatus(ScoringDataDTO.MaritalStatusEnum.MARRIED);

        try {
            calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
        } catch (ScoringException e) {
            throw new IllegalArgumentException("Got unexpected scoring exception!");
        }

        assertEquals(0, calculatedRate.compareTo(expectedRate));
    }

    // Разведен → ставка увеличивается на 1
    @Test
    void calculateCreditRateDivorced() {

        BigDecimal expectedRate = BigDecimal.valueOf(BASE_RATE + DIVORCED_CORRECTION);
        BigDecimal calculatedRate;

        baseRateScoringRequest.setMaritalStatus(ScoringDataDTO.MaritalStatusEnum.DIVORCED);

        try {
            calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
        } catch (ScoringException e) {
            throw new IllegalArgumentException("Got unexpected scoring exception!");
        }

        assertEquals(0, calculatedRate.compareTo(expectedRate));
    }

    // Небинарный → ставка увеличивается на 3
    @Test
    void calculateCreditRateNonBinary() {

        BigDecimal expectedRate = BigDecimal.valueOf(BASE_RATE + NON_BINARY_CORRECTION);
        BigDecimal calculatedRate;

        baseRateScoringRequest.setGender(ScoringDataDTO.GenderEnum.NON_BINARY);

        try {
            calculatedRate = scoringService.calculateRate(baseRateScoringRequest);
        } catch (ScoringException e) {
            throw new IllegalArgumentException("Got unexpected scoring exception!");
        }

        assertEquals(0, calculatedRate.compareTo(expectedRate));
    }

    // Общий стаж менее 12 месяцев → отказ
    @Test
    void calculateCreditRateFewTotalExperience() {

        int numberOfHappenedExceptions = 0;

        String expectedExceptionMessage = ScoringException.ExceptionCause.INSUFFICIENT_EXPERIENCE.getUserFriendlyMessage();

        EmploymentDTO employmentDTO = baseRateScoringRequest.getEmployment();
        employmentDTO.setWorkExperienceTotal(MIN_TOTAL_EXPERIENCE - 1);
        baseRateScoringRequest.setEmployment(employmentDTO);

        try {
            scoringService.calculateRate(baseRateScoringRequest);
        } catch (ScoringException e) {
            assertEquals(expectedExceptionMessage, e.getMessage());
            ++numberOfHappenedExceptions;
        }

        assertEquals(1, numberOfHappenedExceptions);
    }

    // Текущий стаж менее 3 месяцев → отказ
    @Test
    void calculateCreditRateFewCurrentExperience() {

        int numberOfHappenedExceptions = 0;

        String expectedExceptionMessage = ScoringException.ExceptionCause.INSUFFICIENT_EXPERIENCE.getUserFriendlyMessage();

        EmploymentDTO employmentDTO = baseRateScoringRequest.getEmployment();
        employmentDTO.setWorkExperienceTotal(MIN_CURRENT_EXPERIENCE - 1);
        baseRateScoringRequest.setEmployment(employmentDTO);

        try {
            scoringService.calculateRate(baseRateScoringRequest);
        } catch (ScoringException e) {
            assertEquals(expectedExceptionMessage, e.getMessage());
            ++numberOfHappenedExceptions;
        }

        assertEquals(1, numberOfHappenedExceptions);
    }

    // Возраст менее 20 или более 60 лет → отказ
    @Test
    void calculateCreditRateAgeRefuse() {

        int numberOfHappenedExceptions = 0;

        String expectedExceptionMessage = ScoringException.ExceptionCause.UNACCEPTABLE_AGE.getUserFriendlyMessage();

        //checking that person with minimum possible age will pass credit scoring
        baseRateScoringRequest.setBirthdate(LocalDate.now().minusYears(MIN_LOAN_AGE));
        try {
            scoringService.calculateRate(baseRateScoringRequest);
        } catch (ScoringException e) {
            throw new IllegalArgumentException("Minimum possible age should pass scoring.");
        }

        //checking that person with less than minimum possible age will not pass credit scoring
        baseRateScoringRequest.setBirthdate(LocalDate.now().minusYears(MIN_LOAN_AGE - 1));
        try {
            scoringService.calculateRate(baseRateScoringRequest);
        } catch (ScoringException e) {
            assertEquals(expectedExceptionMessage, e.getMessage());
            ++numberOfHappenedExceptions;
        }

        //checking that person with maximum possible age will pass credit scoring
        baseRateScoringRequest.setBirthdate(LocalDate.now().minusYears(MAX_LOAN_AGE));
        try {
            scoringService.calculateRate(baseRateScoringRequest);
        } catch (ScoringException e) {
            throw new IllegalArgumentException("Maximum possible age should pass scoring.");
        }

        //checking that person with more than maximum possible age will not pass credit scoring
        baseRateScoringRequest.setBirthdate(LocalDate.now().minusYears(MAX_LOAN_AGE + 1));
        try {
            scoringService.calculateRate(baseRateScoringRequest);
        } catch (ScoringException e) {
            assertEquals(expectedExceptionMessage, e.getMessage());
            ++numberOfHappenedExceptions;
        }

        assertEquals(2, numberOfHappenedExceptions);
    }

    // Сумма займа больше, чем 20 зарплат → отказ
    @Test
    void calculateCreditRateTooMuchAmountForSalary() {

        int numberOfHappenedExceptions = 0;

        String expectedExceptionMessage = ScoringException.ExceptionCause.INSUFFICIENT_SALARY.getUserFriendlyMessage();

        BigDecimal fineSalary = baseRateScoringRequest.getAmount().divide(SALARY_TO_LOAN_RATE_MAX, MathContext.DECIMAL64);
        BigDecimal fewSalary = fineSalary.subtract(BigDecimal.ONE);

        EmploymentDTO employmentDTO = baseRateScoringRequest.getEmployment();

        //customer with fine salary should pass scoring
        employmentDTO.setSalary(fineSalary);
        baseRateScoringRequest.setEmployment(employmentDTO);
        try {
            scoringService.calculateRate(baseRateScoringRequest);
        } catch (ScoringException e) {
            throw new IllegalArgumentException("Customer with fine salary should pass scoring.");
        }

        //customer with too few salary shouldn't pass scoring
        employmentDTO.setSalary(fewSalary);
        baseRateScoringRequest.setEmployment(employmentDTO);
        try {
            scoringService.calculateRate(baseRateScoringRequest);
        } catch (ScoringException e) {
            assertEquals(expectedExceptionMessage, e.getMessage());
            ++numberOfHappenedExceptions;
        }

        assertEquals(1, numberOfHappenedExceptions);
    }

    // Безработный → отказ
    @Test
    void calculateCreditRateUnemployed() {

        int numberOfHappenedExceptions = 0;

        String expectedExceptionMessage = ScoringException.ExceptionCause.UNACCEPTABLE_EMPLOYER_STATUS.getUserFriendlyMessage();

        EmploymentDTO employmentDTO = baseRateScoringRequest.getEmployment();
        employmentDTO.setEmploymentStatus(EmploymentDTO.EmploymentStatusEnum.UNEMPLOYED);
        baseRateScoringRequest.setEmployment(employmentDTO);

        try {
            scoringService.calculateRate(baseRateScoringRequest);
        } catch (ScoringException e) {
            assertEquals(expectedExceptionMessage, e.getMessage());
            ++numberOfHappenedExceptions;
        }

        assertEquals(1, numberOfHappenedExceptions);
    }

}