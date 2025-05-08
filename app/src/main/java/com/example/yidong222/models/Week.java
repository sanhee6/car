package com.example.yidong222.models;

public class Week {
    private int weekNumber;
    private boolean isSelected;

    public Week(int weekNumber, boolean isSelected) {
        this.weekNumber = weekNumber;
        this.isSelected = isSelected;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}