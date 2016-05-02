package com.hawk.contact.model;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by heyong on 16/4/24.
 */
public class ContactPerson extends ContactModel<ContactPerson> {


    public static final Comparator<ListItem<ContactPerson>> COMPARATOR_LIST_ITEM_DATE_ASC
            = new ListItemAddDateComparator(true);

    private static class ListItemAddDateComparator implements Comparator<ListItem<ContactPerson>> {

        private final boolean ascending;

        ListItemAddDateComparator(boolean ascending) {
            this.ascending = ascending;
        }

        @Override
        public int compare(ListItem<ContactPerson> item1, ListItem<ContactPerson> item2) {
            if (item1.getListType() == ListItem.TYPE_SECTION) {
                return -1;
            } else if (item2.getListType() == ListItem.TYPE_SECTION) {
                return 1;
            } else {
                final Date date1 = item1.getListItem().getDateOfAdd();
                final Date date2 = item2.getListItem().getDateOfAdd();
                if (date1.before(date2)) {
                    return ascending ? -1 : 1;
                } else if (date1.after(date2)) {
                    return ascending ? 1 : -1;
                }
            }

            return 0;
        }
    }

    private Long _id;
    private String name;
    private String pictureUrl;
    private String placeOfBirth;
    private Date dateOfAdd;
    private Date dateOfBirth;
    private Date dateOfDeath;
    private int age;
    private String biography;
    private int pictureType;

    private boolean inCollection;
    private boolean watched;

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public boolean inCollection() {
        return inCollection;
    }

    public void setInCollection(boolean inCollection) {
        this.inCollection = inCollection;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public Date getDateOfAdd() {
        return dateOfAdd;
    }

    public void setDateOfAdd(Date dateOfAdd) {
        this.dateOfAdd = dateOfAdd;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Date getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(Date dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public int getPictureType() {
        return pictureType;
    }

    public void setPictureType(int pictureType) {
        this.pictureType = pictureType;
    }
}
