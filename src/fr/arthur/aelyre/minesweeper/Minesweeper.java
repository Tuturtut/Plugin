package fr.arthur.aelyre.minesweeper;

public class Minesweeper  {

    private MinesweeperSize size = MinesweeperSize.SMALL;


    private enum MinesweeperSize {
        SMALL(10, 10),
        MEDIUM(15, 15),
        LARGE(20, 20);

        private int height;
        private int width;

        MinesweeperSize(int height, int width) {
            this.height = height;
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public void setWidth(int width) {
            this.width = width;
        }
    }
}
