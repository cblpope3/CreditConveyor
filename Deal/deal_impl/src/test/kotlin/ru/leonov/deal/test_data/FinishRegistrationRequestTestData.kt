package ru.leonov.deal.test_data

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.leonov.deal.dto.FinishRegistrationRequestDTO

class FinishRegistrationRequestTestData {

    companion object {
        fun fineRequestJSON(): String {
            return """
                    {
                      "gender": "MALE",
                      "maritalStatus": "MARRIED",
                      "dependentAmount": 4,
                      "passportIssueDate": "2013-05-22",
                      "passportIssueBranch": "УВД №66 г. Бобруйска.",
                      "employment": {
                        "employmentStatus": "UNEMPLOYED",
                        "employerINN": "123456789012",
                        "salary": 45000.34,
                        "position": "OWNER",
                        "workExperienceTotal": 34,
                        "workExperienceCurrent": 10
                      },
                      "account": "234234264363"
                    }
                    """
        }

        fun fineRequestObject(): FinishRegistrationRequestDTO {
            val mapper = jacksonObjectMapper()
            mapper.registerModule(JavaTimeModule())

            return mapper.readValue(fineRequestJSON())
        }

        fun noGenderRequestJSON(): String {
            return """
                    {
                      "maritalStatus": "MARRIED",
                      "dependentAmount": 4,
                      "passportIssueDate": "2013-05-22",
                      "passportIssueBranch": "УВД №66 г. Бобруйска.",
                      "employment": {
                        "employmentStatus": "UNEMPLOYED",
                        "employerINN": "123456789012",
                        "salary": 45000.34,
                        "position": "OWNER",
                        "workExperienceTotal": 34,
                        "workExperienceCurrent": 10
                      },
                      "account": "234234264363"
                    }
                    """
        }

        fun noMStatusRequestJSON(): String {
            return """
                    {
                      "gender": "MALE",
                      "dependentAmount": 4,
                      "passportIssueDate": "2013-05-22",
                      "passportIssueBranch": "УВД №66 г. Бобруйска.",
                      "employment": {
                        "employmentStatus": "UNEMPLOYED",
                        "employerINN": "123456789012",
                        "salary": 45000.34,
                        "position": "OWNER",
                        "workExperienceTotal": 34,
                        "workExperienceCurrent": 10
                      },
                      "account": "234234264363"
                    }
                    """
        }

        fun noDependentAmountRequestJSON(): String {
            return """
                    {
                      "gender": "MALE",
                      "maritalStatus": "MARRIED",
                      "passportIssueDate": "2013-05-22",
                      "passportIssueBranch": "УВД №66 г. Бобруйска.",
                      "employment": {
                        "employmentStatus": "UNEMPLOYED",
                        "employerINN": "123456789012",
                        "salary": 45000.34,
                        "position": "OWNER",
                        "workExperienceTotal": 34,
                        "workExperienceCurrent": 10
                      },
                      "account": "234234264363"
                    }
                    """
        }

        fun noPIDateRequestJSON(): String {
            return """
                    {
                      "gender": "MALE",
                      "maritalStatus": "MARRIED",
                      "dependentAmount": 4,
                      "passportIssueBranch": "УВД №66 г. Бобруйска.",
                      "employment": {
                        "employmentStatus": "UNEMPLOYED",
                        "employerINN": "123456789012",
                        "salary": 45000.34,
                        "position": "OWNER",
                        "workExperienceTotal": 34,
                        "workExperienceCurrent": 10
                      },
                      "account": "234234264363"
                    }
                    """
        }

        fun noPIBranchRequestJSON(): String {
            return """
                    {
                      "gender": "MALE",
                      "maritalStatus": "MARRIED",
                      "dependentAmount": 4,
                      "passportIssueDate": "2013-05-22",
                      "employment": {
                        "employmentStatus": "UNEMPLOYED",
                        "employerINN": "123456789012",
                        "salary": 45000.34,
                        "position": "OWNER",
                        "workExperienceTotal": 34,
                        "workExperienceCurrent": 10
                      },
                      "account": "234234264363"
                    }
                    """
        }

        fun noEmploymentRequestJSON(): String {
            return """
                    {
                      "gender": "MALE",
                      "maritalStatus": "MARRIED",
                      "dependentAmount": 4,
                      "passportIssueDate": "2013-05-22",
                      "passportIssueBranch": "УВД №66 г. Бобруйска.",
                      "account": "234234264363"
                    }
                    """
        }

        fun noEStatusRequestJSON(): String {
            return """
                    {
                      "gender": "MALE",
                      "maritalStatus": "MARRIED",
                      "dependentAmount": 4,
                      "passportIssueDate": "2013-05-22",
                      "passportIssueBranch": "УВД №66 г. Бобруйска.",
                      "employment": {
                        "employerINN": "123456789012",
                        "salary": 45000.34,
                        "position": "OWNER",
                        "workExperienceTotal": 34,
                        "workExperienceCurrent": 10
                      },
                      "account": "234234264363"
                    }
                    """
        }

        fun noEINNRequestJSON(): String {
            return """
                    {
                      "gender": "MALE",
                      "maritalStatus": "MARRIED",
                      "dependentAmount": 4,
                      "passportIssueDate": "2013-05-22",
                      "passportIssueBranch": "УВД №66 г. Бобруйска.",
                      "employment": {
                        "employmentStatus": "UNEMPLOYED",
                        "salary": 45000.34,
                        "position": "OWNER",
                        "workExperienceTotal": 34,
                        "workExperienceCurrent": 10
                      },
                      "account": "234234264363"
                    }
                    """
        }

        fun noESalaryRequestJSON(): String {
            return """
                    {
                      "gender": "MALE",
                      "maritalStatus": "MARRIED",
                      "dependentAmount": 4,
                      "passportIssueDate": "2013-05-22",
                      "passportIssueBranch": "УВД №66 г. Бобруйска.",
                      "employment": {
                        "employmentStatus": "UNEMPLOYED",
                        "employerINN": "123456789012",
                        "position": "OWNER",
                        "workExperienceTotal": 34,
                        "workExperienceCurrent": 10
                      },
                      "account": "234234264363"
                    }
                    """
        }

        fun noEPositionRequestJSON(): String {
            return """
                    {
                      "gender": "MALE",
                      "maritalStatus": "MARRIED",
                      "dependentAmount": 4,
                      "passportIssueDate": "2013-05-22",
                      "passportIssueBranch": "УВД №66 г. Бобруйска.",
                      "employment": {
                        "employmentStatus": "UNEMPLOYED",
                        "employerINN": "123456789012",
                        "salary": 45000.34,
                        "workExperienceTotal": 34,
                        "workExperienceCurrent": 10
                      },
                      "account": "234234264363"
                    }
                    """
        }

        fun noEWETotalRequestJSON(): String {
            return """
                    {
                      "gender": "MALE",
                      "maritalStatus": "MARRIED",
                      "dependentAmount": 4,
                      "passportIssueDate": "2013-05-22",
                      "passportIssueBranch": "УВД №66 г. Бобруйска.",
                      "employment": {
                        "employmentStatus": "UNEMPLOYED",
                        "employerINN": "123456789012",
                        "salary": 45000.34,
                        "position": "OWNER",
                        "workExperienceCurrent": 10
                      },
                      "account": "234234264363"
                    }
                    """
        }

        fun noEWECurrentRequestJSON(): String {
            return """
                    {
                      "gender": "MALE",
                      "maritalStatus": "MARRIED",
                      "dependentAmount": 4,
                      "passportIssueDate": "2013-05-22",
                      "passportIssueBranch": "УВД №66 г. Бобруйска.",
                      "employment": {
                        "employmentStatus": "UNEMPLOYED",
                        "employerINN": "123456789012",
                        "salary": 45000.34,
                        "position": "OWNER",
                        "workExperienceTotal": 34
                      },
                      "account": "234234264363"
                    }
                    """
        }

        fun noAccountRequestJSON(): String {
            return """
                    {
                      "gender": "MALE",
                      "maritalStatus": "MARRIED",
                      "dependentAmount": 4,
                      "passportIssueDate": "2013-05-22",
                      "passportIssueBranch": "УВД №66 г. Бобруйска.",
                      "employment": {
                        "employmentStatus": "UNEMPLOYED",
                        "employerINN": "123456789012",
                        "salary": 45000.34,
                        "position": "OWNER",
                        "workExperienceTotal": 34,
                        "workExperienceCurrent": 10
                      }
                    }
                    """
        }

        fun wrongGenderRequestJSON(): String {
            return """
                    {
                      "gender": "abracadabra",
                      "maritalStatus": "MARRIED",
                      "dependentAmount": 4,
                      "passportIssueDate": "2013-05-22",
                      "passportIssueBranch": "УВД №66 г. Бобруйска.",
                      "employment": {
                        "employmentStatus": "UNEMPLOYED",
                        "employerINN": "123456789012",
                        "salary": 45000.34,
                        "position": "OWNER",
                        "workExperienceTotal": 34,
                        "workExperienceCurrent": 10
                      },
                      "account": "234234264363"
                    }
                    """
        }

        fun wrongMStatusRequestJSON(): String {
            return """
                    {
                      "gender": "MALE",
                      "maritalStatus": "abracadabra",
                      "dependentAmount": 4,
                      "passportIssueDate": "2013-05-22",
                      "passportIssueBranch": "УВД №66 г. Бобруйска.",
                      "employment": {
                        "employmentStatus": "UNEMPLOYED",
                        "employerINN": "123456789012",
                        "salary": 45000.34,
                        "position": "OWNER",
                        "workExperienceTotal": 34,
                        "workExperienceCurrent": 10
                      },
                      "account": "234234264363"
                    }
                    """
        }

        fun wrongEStatusRequestJSON(): String {
            return """
                    {
                      "gender": "MALE",
                      "maritalStatus": "MARRIED",
                      "dependentAmount": 4,
                      "passportIssueDate": "2013-05-22",
                      "passportIssueBranch": "УВД №66 г. Бобруйска.",
                      "employment": {
                        "employmentStatus": "abracadabra",
                        "employerINN": "123456789012",
                        "salary": 45000.34,
                        "position": "OWNER",
                        "workExperienceTotal": 34,
                        "workExperienceCurrent": 10
                      },
                      "account": "234234264363"
                    }
                    """
        }

        fun wrongEPositionRequestJSON(): String {
            return """
                    {
                      "gender": "MALE",
                      "maritalStatus": "MARRIED",
                      "dependentAmount": 4,
                      "passportIssueDate": "2013-05-22",
                      "passportIssueBranch": "УВД №66 г. Бобруйска.",
                      "employment": {
                        "employmentStatus": "UNEMPLOYED",
                        "employerINN": "123456789012",
                        "salary": 45000.34,
                        "position": "abracadabra",
                        "workExperienceTotal": 34,
                        "workExperienceCurrent": 10
                      },
                      "account": "234234264363"
                    }
                    """
        }

        fun wrongAccountRequestJSON(): String {
            return """
                    {
                      "gender": "MALE",
                      "maritalStatus": "MARRIED",
                      "dependentAmount": 4,
                      "passportIssueDate": "2013-05-22",
                      "passportIssueBranch": "УВД №66 г. Бобруйска.",
                      "employment": {
                        "employmentStatus": "UNEMPLOYED",
                        "employerINN": "123456789012",
                        "salary": 45000.34,
                        "position": "OWNER",
                        "workExperienceTotal": 34,
                        "workExperienceCurrent": 10
                      },
                      "account": "abracadabra"
                    }
                    """
        }

    }
}
