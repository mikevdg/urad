package gulik.demo;

import java.util.Date;

public class Vegetable {
    private String name;
    private String colour;  // TODO: an enum!
    private Boolean childrenLikeIt;
    private Integer weight;
    private Date planted; // TODO: Joda time.

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public Boolean isChildrenLikeIt() {
        return childrenLikeIt;
    }

    public void setChildrenLikeIt(Boolean childrenLikeIt) {
        this.childrenLikeIt = childrenLikeIt;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Date getPlanted() {
        return planted;
    }

    public void setPlanted(Date planted) {
        this.planted = planted;
    }

    @Override
    public String toString() {
        return "Vegetable{" +
                "name='" + name + '\'' +
                '}';
    }
}
