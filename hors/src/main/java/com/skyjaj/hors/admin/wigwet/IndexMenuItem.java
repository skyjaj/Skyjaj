package com.skyjaj.hors.admin.wigwet;

public class IndexMenuItem {

        public IndexMenuItem(String text, boolean isSelected, int icon, int iconSelected) {
            this.text = text;
            this.isSelected = isSelected;
            this.icon = icon;
            this.iconSelected = iconSelected;
        }

        boolean isSelected;
        String text;
        int icon;
        int iconSelected;
    }
