//package assignment1;
import java.util.ArrayList;
public class Peer {
        private int peerid;
        private int firstsucc;
        private int secondsucc;
        private int ping; 
        private int firstpred;
        private int secondpred;
        private boolean firstalive;
        private boolean secondalive;
        private int packetLoss1;
        private int packetLoss2;
        private ArrayList<String> files = new ArrayList<String>();

        public Peer (int peerid, int firstsucc, int secondsucc, int ping) {
            this.peerid = peerid;
            this.firstsucc = firstsucc;
            this.secondsucc = secondsucc;
            this.ping = ping;

        }
        
        public Peer (int peerid, int ping) {
            this.peerid = peerid;
            this.ping = ping;
        }
        
        public int get_peerid() {
            return this.peerid;
        }

        public int get_firstsucc() {
            return this.firstsucc;
        }
        
        public int get_secondsucc() {
            return this.secondsucc;
        }

        public int get_ping(){
            return this.ping;
        }

        public int get_firstpred() {
            return this.firstpred;
        }

        public int get_secondpred() {
            return this.secondpred;
        }

        public boolean get_firstalive() {
            return this.firstalive;
        }

        public boolean get_secondalive() {
            return this.secondalive;
        }

        public int get_packetLoss1() {
            return this.packetLoss1;
        }

        public int get_packetLoss2() {
            return this.packetLoss2;
        }

        public ArrayList get_files() {
            return this.files;
        }

        public void set_peerid (int peerid) {
            this.peerid = peerid;
        }

        public void set_firstsucc (int firstsucc) {
            this.firstsucc = firstsucc;
        }

        public void set_secondsucc (int secondsucc) {
            this.secondsucc = secondsucc;
        }

        public void set_ping(int ping) {
            this.ping = ping;
        }
        
        public void set_firstpred(int firstpred) {
            this.firstpred = firstpred;
        }

        public void set_secondpred(int secondpred) {
            this.secondpred = secondpred;
        }

        public void set_firstalive(boolean firstalive) {
            this.firstalive = firstalive;
        }

        public void set_secondalive(boolean secondalive) {
            this.secondalive = secondalive;
        }

        public void set_packetLoss1 (int packetLoss1) {
            this.packetLoss1 = packetLoss1;
        }

        public void set_packetLoss2 (int packetLoss2) {
            this.packetLoss2 = packetLoss2;
        }

        public void add_files(String filename) {
            this.files.add(filename);
        }


}