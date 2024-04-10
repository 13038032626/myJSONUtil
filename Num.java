public class Num {
    private int big;
    private int small;

    public int getBig() {
        return big;
    }

    public void setBig(int big) {
        this.big = big;
    }

    public int getSmall() {
        return small;
    }

    @Override
    public String toString() {
        return "Num{" +
                "big=" + big +
                ", small=" + small +
                '}';
    }

    public void setSmall(int small) {
        this.small = small;
    }
}
