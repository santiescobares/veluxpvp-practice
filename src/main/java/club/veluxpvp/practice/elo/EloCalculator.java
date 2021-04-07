package club.veluxpvp.practice.elo;

public class EloCalculator {

	public static int calculate(int winnerElo, int loserElo) {
        double winnerQ = Math.pow(10, ((double) winnerElo) / 300D);
        double loserQ = Math.pow(10, ((double) loserElo) / 300D);

        double winnerE = winnerQ / (winnerQ + loserQ);
        double loserE = loserQ / (winnerQ + loserQ);

        int winnerGain = (int) (35 * (1 - winnerE));
        int loserGain = (int) (35 * (0 - loserE));

        winnerGain = Math.min(winnerGain, 25);
        winnerGain = Math.max(winnerGain, 7);

        loserGain = Math.min(loserGain, -7);
        loserGain = Math.max(loserGain, -25);
        
        return winnerGain;
	}
	
	/*
	public static int calculate(int winnerElo, int loserElo) {
		int eloDiff = winnerElo >= loserElo ? winnerElo - loserElo : loserElo - winnerElo;
		int addedElo = 0;
		
		if(winnerElo <= (loserElo + 10) && winnerElo >= (loserElo - 10)) {
			addedElo += getRandom(12, 15);
		} else if(winnerElo <= (loserElo + 6) && winnerElo >= (loserElo - 6)) {
			addedElo += getRandom(9, 12);
		} else if(winnerElo <= loserElo) {
			for(int i = 0; i <= eloDiff; i += 3) {
				addedElo += 2;
			}
			
			if(addedElo == 0) addedElo = 1;
			if(addedElo > 25) addedElo = 25;
		} else {
			for(int i = 0; i <= eloDiff; i += 8) {
				addedElo++;
			}
			
			if(addedElo == 0) addedElo = 1;
			if(addedElo > 10) addedElo = 10;
		}
		
		return addedElo;
	}
	
	private static int getRandom(int min, int max) {
		return new Random().nextInt(max - min) + min;
	}
	*/
}
