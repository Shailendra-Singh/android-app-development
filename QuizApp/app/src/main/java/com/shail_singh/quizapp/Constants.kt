package com.shail_singh.quizapp

object Constants {
    private const val GENERIC_QUESTION_STRING: String = "What country does this flag belong to?"
    const val USER_NAME: String = "user_name"
    const val TOTAL_QUESTIONS: String = "total_questions"
    const val CORRECT_ANSWERS: String = "correct_answers"

    fun getQuestions(): ArrayList<Question> {

        val questions = ArrayList<Question>()

        // 01
        questions.add(
            Question(
                id = 1,
                question = GENERIC_QUESTION_STRING,
                image = R.drawable.ic_flag_of_argentina,
                optionA = "Argentina",
                optionB = "Australia",
                optionC = "Armenia",
                optionD = "Austria",
                correctAnswer = 1
            )
        )

        // 02
        questions.add(
            Question(
                id = 1,
                question = GENERIC_QUESTION_STRING,
                image = R.drawable.ic_flag_of_australia,
                optionA = "Angola",
                optionB = "Austria",
                optionC = "Australia",
                optionD = "Armenia",
                correctAnswer = 3
            )
        )


        // 03
        questions.add(
            Question(
                id = 3,
                question = GENERIC_QUESTION_STRING,
                image = R.drawable.ic_flag_of_brazil,
                optionA = "Belarus",
                optionB = "Belize",
                optionC = "Brunei",
                optionD = "Brazil",
                correctAnswer = 4
            )
        )

        // 04
        questions.add(
            Question(
                id = 4,
                question = GENERIC_QUESTION_STRING,
                image = R.drawable.ic_flag_of_belgium,
                optionA = "Bahamas",
                optionB = "Belgium",
                optionC = "Barbados",
                optionD = "Belize",
                correctAnswer = 2
            )
        )

        // 05
        questions.add(
            Question(
                id = 5,
                question = GENERIC_QUESTION_STRING,
                image = R.drawable.ic_flag_of_fiji,
                optionA = "Gabon",
                optionB = "France",
                optionC = "Fiji",
                optionD = "Finland",
                correctAnswer = 3
            )
        )

        // 06
        questions.add(
            Question(
                id = 6,
                question = GENERIC_QUESTION_STRING,
                image = R.drawable.ic_flag_of_germany,
                optionA = "Germany",
                optionB = "Georgia",
                optionC = "Greece",
                optionD = "None of Above",
                correctAnswer = 1
            )
        )

        // 07
        questions.add(
            Question(
                id = 7,
                question = GENERIC_QUESTION_STRING,
                image = R.drawable.ic_flag_of_denmark,
                optionA = "Dominica",
                optionB = "Egypt",
                optionC = "Denmark",
                optionD = "Ethiopia",
                correctAnswer = 3
            )
        )

        // 08
        questions.add(
            Question(
                id = 8,
                question = GENERIC_QUESTION_STRING,
                image = R.drawable.ic_flag_of_india,
                optionA = "Ireland",
                optionB = "Iran",
                optionC = "Hungary",
                optionD = "India",
                correctAnswer = 4
            )
        )

        // 09
        questions.add(
            Question(
                id = 9,
                question = GENERIC_QUESTION_STRING,
                image = R.drawable.ic_flag_of_new_zealand,
                optionA = "Australia",
                optionB = "New Zealand",
                optionC = "Tuvalu",
                optionD = "United States of America",
                correctAnswer = 2
            )
        )

        // 10
        questions.add(
            Question(
                id = 10,
                question = GENERIC_QUESTION_STRING,
                image = R.drawable.ic_flag_of_kuwait,
                optionA = "Kuwait",
                optionB = "Jordan",
                optionC = "Sudan",
                optionD = "Palestine",
                correctAnswer = 1
            )
        )

        return questions
    }
}