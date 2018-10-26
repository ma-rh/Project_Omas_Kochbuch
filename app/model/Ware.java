package model;

public class Ware {

    private int id;
    private String warename;
    private int price;

    /**
     * Constructor
     *
     * @param id
     * @param warename
     * @param price
     */
    public Ware(int id, String warename, int price) {
        this.id = id;
        this.warename = warename;
        this.price = price;
    }

    //------------------- GETTER AND SETTER --------------

    public String getWarename() {
        return warename;
    }

    public int getId() {
        return id;
    }

    public int getPrice() {
        return price;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ware ware = (Ware) o;

        if (id == ware.id) {
            return true;
        }
        return false;
    }

}