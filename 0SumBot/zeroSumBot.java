/**
 * Copyright 2017 Aly Shmahell
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http:#www.apache.org/licenses/LICENSE-2.0 . Unless
 * required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 * 
 * Author: Aly Shmahell
 */

import java.util.*;
import java.io.*;

public class zeroSumBot {

  public static void DoTurn(PlanetWars pw) {
 
	/**
	 * Initial Parameters
	 */
	Random rand = new Random();
    Planet base = null;
    Planet target = null;
    int baseFleet = 0;
    boolean AttackMode = false;
    
    /***
     * Optimized Parameters : generated by the Genetic Algorithm to be used as seed for the Stochastic Parameters
     * "o" prefix is notation for Optimized
     * "R" affix is notation for Range of stocasticity
     */
    /**
     * optimized for Player's total number of ships to total ship production ratio
     */
     double oPWNumShipsProduction1 = 0.5;

    /**
     * optimized for player's base planet's total number of ships
     */
     int oPNumShips = 20;
     int oPNumShipsR = 5;
    /**
     * optimized for player's base planet's growth rate
     */
     int oPGrowthRate = 20;
     int oPGrowthRateR = 5;
     
    /**
     * optimized for enemy's target planet's total number of ships
     */
     int oENumShips = 20;
     int oENumShipsR = 5;
    /**
     * optimized for enemy's target planet's growth rate
     */
     int oEGrowthRate = 20;
     int oEGrowthRateR = 5;
     
    /**
     * optimized for neutrals' target planet's total number of ships
     */
     int oNNumShips = 20;
     int oNNumShipsR = 5;
    /**
     * optimized for neutrals' target planet's growth rate
     */
     int oNGrowthRate = 20;
     int oNGrowthRateR = 5;
     
    /**
     * optimized for good striking distance
     */
     int oDistance = 40;
     int oDistanceR = 5;
     
    /**
     * optimized for player's total number of fleets
     */
     int numFleets = 5;
    /**
     * optimized for player's division of base ships into remaining base ships and base fleet
     */
     int fleetDivider = 2;
     
    /***
     * Stochastic Parameters: seeded by the 
     * "s" prefix is notation for Stochastic
     */
    double sPWNumShipsProduction1 = oPWNumShipsProduction1 + rand.nextDouble();
    
    int sPNumShips = oPNumShips + rand.nextInt(oPNumShipsR);
    int sPGrowthRate = oPGrowthRate + rand.nextInt(oPGrowthRateR);
    
    int sENumShips = oPNumShips + rand.nextInt(oPNumShipsR);
    int sEGrowthRate = oPGrowthRate + rand.nextInt(oPGrowthRateR);
    
    int sNNumShips = oPNumShips + rand.nextInt(oPNumShipsR);
    int sNGrowthRate = oPGrowthRate + rand.nextInt(oPGrowthRateR);

    int sDistance = oDistance + rand.nextInt(oDistanceR);
    
    /**
     * attack mode selection
     */
    if( (double) pw.NumShips(1)/(1+pw.Production(1)) > sPWNumShipsProduction1)
    	AttackMode = true;
    else
    	AttackMode = false;
   
    /**
     * base selection
     */
    for(Planet p : pw.MyPlanets()){
      if(base==null){
          if(p.NumShips() > sPNumShips && p.GrowthRate() > sPGrowthRate)
      	      base = p;
      }
      else if(p.NumShips() > base.NumShips() && p.GrowthRate() > base.GrowthRate())
      	  	  base = p;
     }
    /**
     * base force selection
     */
    if(base==null)
    {
    	double baseScore = Double.MIN_VALUE;
        for (Planet p : pw.MyPlanets()) {
            double score = (double) p.NumShips() / (1 + p.GrowthRate());
            if (score > baseScore) {
                baseScore = score;
                base = p;
            }
        }
    }
    /**
     * fleet selection
     */
    baseFleet = (int) base.NumShips()/fleetDivider;
    
    
    if(AttackMode == true){
    	/**
    	 * target selection from enemy
    	 */
		for(Planet e : pw.EnemyPlanets()){
		  if(target==null){
			  if(e.NumShips() < sENumShips && e.GrowthRate() < sEGrowthRate && pw.Distance(base,e) < sDistance)
				  target = e;
		  }
		  else if(e.NumShips() < target.NumShips() && e.GrowthRate() < target.GrowthRate() && pw.Distance(base,e) < pw.Distance(base,target))
				  target = e;
		 } 
		/**
		 * target force selection from neutrals
		 */
		if(target == null)
		{
			for(Planet n : pw.NeutralPlanets()){
			  if(target==null){
				  if(n.NumShips() < sENumShips && n.GrowthRate() < sEGrowthRate  && pw.Distance(base,n) < sDistance)
					  target = n;
			  }
			  else if(n.NumShips() < target.NumShips() && n.GrowthRate() < target.GrowthRate() && pw.Distance(base,n) < pw.Distance(base,target))
					  target = n;
			 } 
		}
		/**
		 * target force selection from Not My Planets in case of no target
		 */
		if(target == null)
		{
			double npScore = Double.MIN_VALUE;
			for (Planet np : pw.NotMyPlanets()) {
				double score = (double) (1 + np.GrowthRate()) / np.NumShips();
				if (score > npScore) {
					npScore = score;
					target = np;
				}
			}	
		}
    }
    
    if(AttackMode == false){
		/**
		 * ally selection
		 */
		for(Planet p : pw.MyPlanets()){
		  if(target==null){
			  if(p.NumShips() < sPNumShips && p.GrowthRate() < sPGrowthRate  && pw.Distance(base,p) < sDistance)
				  target = p;
		  }
		  else if(p.NumShips() < target.NumShips() && p.GrowthRate() < target.GrowthRate() && pw.Distance(base,p) < pw.Distance(base,target))
				  target = p;
		 }
		/**
		 * ally force selection
		 */
		if(target==null)
		{
			double targetScore = Double.MIN_VALUE;
			for (Planet p : pw.MyPlanets()) {
				double score = (double) p.NumShips() / (1 + p.GrowthRate());
				if (score < targetScore) {
					targetScore = score;
					target = p;
				}
			}
		}     
    }
    
    /**
     * send fleet from base to target
     */
    if (base != null && target != null)
      pw.IssueOrder(base, target, baseFleet);
    else
    	return;
  }
  
  public static void main(String[] args) {
    String line = "";
    String message = "";
    int c;
    try {
      while ((c = System.in.read()) >= 0) {
        switch (c) {
          case '\n':
            if (line.equals("go")) {
            PlanetWars pw = new PlanetWars(message);
            DoTurn(pw);
            pw.FinishTurn();
            message = "";
          } else {
            message += line + "\n";
          }
          line = "";
          break;
          default:
            line += (char) c;
            break;
        }
      }
    } catch (Exception e) {
    }
  }
}