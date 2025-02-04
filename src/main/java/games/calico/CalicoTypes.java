package games.calico;

import utilities.Vector2D;

public class CalicoTypes {

    // Odd r: (odd rows offset to the right)
    public static Vector2D[][] neighbor_directions = new Vector2D[][] {
        {
            new Vector2D(1, 0),
            new Vector2D(0, -1),
            new Vector2D(-1, -1),
            new Vector2D(-1, 0),
            new Vector2D(-1, 1),
            new Vector2D(0, 1)
        },
        {
            new Vector2D(1, 0),
            new Vector2D(1, -1),
            new Vector2D(0, -1),
            new Vector2D(-1, 0),
            new Vector2D(0, 1),
            new Vector2D(1, 1)
        }
    };

    // Enums
    public enum  DesignGoalTile{
        AAA_BBB ("data/calico/images/designTiles/aaa_bbb.png", 8, 13),
        AA_BB_CC ("data/calico/images/designTiles/aa_bb_cc.png", 7, 11),
        NOT ("data/calico/images/designTiles/not.png", 10, 15),
        AAAA_BB ("data/calico/images/designTiles/aaaa_bb.png", 8, 14),
        AAA_BB_C ("data/calico/images/designTiles/aaa_bb_c.png", 7, 11),
        AA_BB_C_D ("data/calico/images/designTiles/aa_bb_c_d.png", 5, 8);

        String imagePath;
        int oneGoal;
        int twoGoal;

        DesignGoalTile(String imagePath, int oneGoal, int twoGoal) {
            this.imagePath = imagePath;
            this.oneGoal = oneGoal;
            this.twoGoal = twoGoal;
        }

        public String getImagePath() {
            return imagePath;
        }

        public int getGoalOne() {
            return oneGoal;
        }

        public int getGoalTwo() {
            return twoGoal;
        }
    }

    public enum BoardTypes{
        blue ("data/calico/images/tiles/board/blue.png"),
        green ("data/calico/images/tiles/board/green.png"),
        purple ("data/calico/images/tiles/board/purple.png"),
        yellow ("data/calico/images/tiles/board/yellow.png");

        String imagePath;

        BoardTypes(String imagePath) {
            this.imagePath = imagePath;
        }

        public String getImagePath() {
            return imagePath;
        }
    }

    public enum TileColour{
        DBlue,
        Green,
        LBlue,
        Yellow,
        Magenta,
        Purple,
        Null
    }

    public enum TilePattern{
        Flowers,
        Dots,
        Vines,
        Stripes,
        Quatrefoil,
        Ferns,
        Null
    }

    public enum Button{
        Blueberry,
        Drop,
        Leaf,
        Flower,
        Moon,
        Mushroom,
        Rainbow
    }

    public enum Cat{
        Millie ("data/calico/images/cats/millie.png", 3, 3),
        Tibbit ("data/calico/images/cats/tibbit.png", 5, 4),
        Coconut ("data/calico/images/cats/coconut.png", 7, 5),
        Tecolote ("data/calico/images/cats/tecolote.png", 7, 4), //in a straightline
        Callie ("data/calico/images/cats/callie.png", 3, 3), //in a T shape
        Rumi ("data/calico/images/cats/rumi.png", 5, 3), //in a stright line
        Gwen ("data/calico/images/cats/gwen.png", 11, 7),
        Cira ("data/calico/images/cats/cira.png", 9, 6),
        Leo ("data/calico/images/cats/leo.png", 11, 5), //in a stright line
        Almond ("data/calico/images/cats/almond.png", 9, 5); //pyramid

        String imagePath;
        int points;
        int arrangement; //TODO: figure out how the more unique arrangements are going to work

        Cat(String imagePath, int points, int arrangement) {
            this.imagePath = imagePath;
            this.points = points;
            this.arrangement = arrangement;
        }

        public String getImagePath() {
            return imagePath;
        }

        public String getName() {
            return this.toString();
        }

        public int getPoints(){
            return points;
        }

        public int getArrangement() {
            return arrangement;
        }
    }
}
