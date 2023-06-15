package com.shail_singh.a7minworkout

object Constants {
    const val REST_DURATION: Long = 10000
    const val EXERCISE_DURATION: Long = 30000
    const val TIME_INTERVAL_DURATION: Long = 1000
    const val ROOT_PROJECT_LOCATION: String = "android.resource://com.shail_singh.a7minworkout/"

    fun defaultExerciseList(): ArrayList<ExerciseModel> {
        val exerciseList = ArrayList<ExerciseModel>()
        exerciseList.add(ExerciseModel(1, "Side Stretches", R.drawable.side_strech))
        exerciseList.add(ExerciseModel(2, "Yoga Sunrise Pose", R.drawable.yoga_sunrises))
        exerciseList.add(ExerciseModel(3, "Double Hand Stand", R.drawable.double_hand_stand))
        exerciseList.add(ExerciseModel(4, "Rope Swings", R.drawable.rope_swings))
        exerciseList.add(ExerciseModel(5, "Barbell Press", R.drawable.barbell_press))
        exerciseList.add(ExerciseModel(6, "Reverse Glute Stretch", R.drawable.reverse_glute_strech))
        exerciseList.add(ExerciseModel(7, "Dumbbell Curls", R.drawable.dumbbell_curls))
        exerciseList.add(ExerciseModel(8, "Hand Push-ups", R.drawable.hand_pushups))
        exerciseList.add(ExerciseModel(9, "Yoga Pranamasana", R.drawable.single_leg_stand))
        exerciseList.add(ExerciseModel(10, "Yoga Hand Stand Dance Pose", R.drawable.yoga_hand_stand_dancer_pose))
        exerciseList.add(ExerciseModel(11, "Ab Crunches", R.drawable.crunches))
        exerciseList.add(ExerciseModel(12, "Shadow Boxing", R.drawable.shadow_boxing))
        return exerciseList
    }
}