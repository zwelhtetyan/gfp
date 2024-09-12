package me.ch3;

import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        List<String> planA = new ArrayList<>();

        planA.add("Paris");
        planA.add("Berlin");
        planA.add("Kraków");

        List<String> planB = replan(planA, "Vienna", "Kraków");

        System.out.println("Plan A: " + planA);
        System.out.println("Plan B:" + planB);
    }

    static List<String> replan(
        List<String> oldPlan,
        String newPlan,
        String before
    ) {
        List<String> plans = new ArrayList<>(oldPlan);
        plans.add(plans.indexOf(before), newPlan);

        return plans;
    }
}
