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

    public static int[][] designLoc = {{3,2}, {4,3}, {2,4}};

    // Enums
    public enum  DesignGoalTile{
        AAA_BBB ("data/calico/images/designTiles/aaa_bbb.png", new int[]{3,3}, 8, 13),
        AA_BB_CC ("data/calico/images/designTiles/aa_bb_cc.png", new int[]{2,2,2}, 7, 11),
        NOT ("data/calico/images/designTiles/not.png", new int[]{1,1,1,1,1,1}, 10, 15),
        AAAA_BB ("data/calico/images/designTiles/aaaa_bb.png", new int[]{4,2}, 8, 14),
        AAA_BB_C ("data/calico/images/designTiles/aaa_bb_c.png", new int[]{3,2,1}, 7, 11),
        AA_BB_C_D ("data/calico/images/designTiles/aa_bb_c_d.png", new int[]{2,2,1,1}, 5, 8);

        String imagePath;
        int[] orderArr;
        int oneGoal;
        int twoGoal;

        DesignGoalTile(String imagePath, int[] orderArr, int oneGoal, int twoGoal) {
            this.imagePath = imagePath;
            this.orderArr = orderArr;
            this.oneGoal = oneGoal;
            this.twoGoal = twoGoal;
        }

        public String getImagePath() {
            return imagePath;
        }

        public int[] getOrderArr() {
            return orderArr;
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
        DBlue (Button.Blueberry),
        Green (Button.Leaf),
        LBlue (Button.Drop),
        Yellow (Button.Moon),
        Magenta (Button.Flower),
        Purple (Button.Mushroom),
        Null (Button.Rainbow); //prevent errors

        Button button;

        TileColour(Button colourButton) {
            this.button = colourButton;
        }

        public Button getButton(){
            return button;
        }
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
        Blueberry ("data/calico/images/buttons/blueberry.png"),
        Drop ("data/calico/images/buttons/drop.png"),
        Leaf ("data/calico/images/buttons/leaf.png"),
        Flower ("data/calico/images/buttons/flower.png"),
        Moon ("data/calico/images/buttons/moon.png"),
        Mushroom ("data/calico/images/buttons/mushroom.png"),
        Rainbow ("data/calico/images/buttons/rainbow.png");

        String imagePath;

        Button(String imagePath) {
            this.imagePath = imagePath;
        }

        public String getImagePath() {
            return imagePath;
        }
    }

        //hard coding in non-patch cat arrangements 

        private static int[][][][] RumiArrangement = new int[][][][] {
            {   //even
                {{0,0}, {-1, 0}, {-2, 0}},
                {{0, 0}, {1, 0}, {2, 0}},
                {{0, 0}, {1, 0}, {-1, 0}}
            },
            {   //odd - same as even but added for continunity
                {{0,0}, {-1, 0}, {-2, 0}},
                {{0, 0}, {1, 0}, {2, 0}},
                {{0, 0}, {1, 0}, {-1, 0}}
            }
        };
    
        private static int[][][][] TecoloteArrangement = new int[][][][] {
            //even
            {
                {{0,0}, {-1, 0}, {-2, 0}, {1, 0}},
                {{0, 0}, {1, 0}, {2, 0}, {3, 0}},
                {{0, 0}, {1, 0}, {-1, 0}, {2, 0}},
                {{0, 0}, {-1, 0}, {-2, 0}, {-3, 0}}
            },
            {//odd
                {{0,0}, {-1, 0}, {-2, 0}, {1, 0}},
                {{0, 0}, {1, 0}, {2, 0}, {3, 0}},
                {{0, 0}, {1, 0}, {-1, 0}, {2, 0}},
                {{0, 0}, {-1, 0}, {-2, 0}, {-3, 0}}
            }
        };
    
        private static int[][][][] LeoArrangement = new int[][][][] {
            //even - check
            {
                {{0,0}, {-1, 0}, {-2, 0}, {1, 0}, {-3, 0}},
                {{0, 0}, {1, 0}, {2, 0}, {3, 0}, {-1, 0}},
                {{0, 0}, {1, 0}, {-1, 0}, {2, 0}, {-2,0}},
                {{0, 0}, {-1, 0}, {-2, 0}, {-3, 0}, {-4,0}}
            },
            {//odd
                {{0,0}, {-1, 0}, {-2, 0}, {1, 0}, {-3, 0}},
                {{0, 0}, {1, 0}, {2, 0}, {3, 0}, {-1, 0}},
                {{0, 0}, {1, 0}, {-1, 0}, {2, 0}, {-2,0}},
                {{0, 0}, {-1, 0}, {-2, 0}, {-3, 0}, {-4,0}}
            }
    
        };
    
        ///two variants - even and odd - all need to be checked
        /// using this for testing rotations
        public static int[][][][] CallieArrangement = new int[][][][] {
            //even
            {
                {{0, 0}, {0,-1}, {-1, -1}},
            },
            { //odd
    
                {{0, 0}, {0, -1}, {1, -1}}
            }
    
        };

        private static int[][][][] AlmondArrangement = new int[][][][] {
            //even
            {
                {{0, 0}, {1,0}, {-1, 1}, {0,1}, {1,1}},  
                {{0,0}, {-1, 0}, {-2,1}, {-1,1}, {0,1}},
                {{0, 0}, {0,-1}, {1, -1}, {1,0}, {2,0}},
                {{0, 0}, {-1,0}, {-1, -1}, {0,-1}, {1,0}},
                {{0, 0}, {-1,-1}, {-1, 0}, {-2, -1}, {-2,0}},
                
            },
            { //odd
                {{0, 0}, {1,0}, {0, 1}, {1,1}, {2,1}},
                {{0, 0}, {-1,0}, {-1, 1}, {0,1}, {1,1}},
                {{0, 0}, {1,-1}, {1, 0}, {2,-1}, {2,0}},
                {{0, 0}, {0,-1}, {-1, 0}, {1,-1}, {1,0}},
                {{0, 0}, {-1,0}, {-2, 0}, {0,-1}, {-1,-1}},
            }
    
        };

    public enum Cat{
        Millie ("data/calico/images/cats/millie.png", "data/calico/images/cats/token/millie.png", 3, new int[][][][] {{{{3}}}}, true),
        Tibbit ("data/calico/images/cats/tibbit.png", "data/calico/images/cats/token/tibbit.png", 5,new int[][][][] {{{{4}}}}, true),
        Coconut ("data/calico/images/cats/coconut.png", "data/calico/images/cats/token/coconut.png", 7, new int[][][][] {{{{5}}}}, true),
        Tecolote ("data/calico/images/cats/tecolote.png", "data/calico/images/cats/token/tecolote.png", 7, TecoloteArrangement, false), //in a straightline
        Callie ("data/calico/images/cats/callie.png", "data/calico/images/cats/token/callie.png", 3, CallieArrangement, false), //in a T shape
        Rumi ("data/calico/images/cats/rumi.png", "data/calico/images/cats/token/rumi.png", 5, RumiArrangement, false), //in a stright line
        Gwen ("data/calico/images/cats/gwen.png", "data/calico/images/cats/token/gwen.png", 11, new int[][][][] {{{{7}}}}, true),
        Cira ("data/calico/images/cats/cira.png", "data/calico/images/cats/token/cira.png", 9, new int[][][][] {{{{6}}}}, true),
        Leo ("data/calico/images/cats/leo.png", "data/calico/images/cats/token/leo.png", 11, LeoArrangement, false), //in a stright line
        Almond ("data/calico/images/cats/almond.png", "data/calico/images/cats/token/almond.png", 9, AlmondArrangement, false); //pyramid

        String imagePath;
        String tokenPath;
        int points;
        int[][][][] arrangement; //arrangement or size of patches
        boolean patchVer; //is a patch count or specific shape

        Cat(String imagePath, String tokenPath, int points, int[][][][] arrangement, boolean patchVer) {
            this.imagePath = imagePath;
            this.tokenPath = tokenPath;
            this.points = points;
            this.arrangement = arrangement;
            this.patchVer = patchVer;
        }

        public String getImagePath() {
            return imagePath;
        }

        public String getTokenPath() {
            return tokenPath;
        }

        public String getName() {
            return this.toString();
        }

        public int getPoints(){
            return points;
        }

        public int[][][][] getArrangement() {
            return arrangement;
        }

        public boolean getPatchVer() {
            return patchVer;
        }
    }

}
