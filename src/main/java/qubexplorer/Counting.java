package qubexplorer;

import java.util.HashMap;
import java.util.Map;
import org.sonar.wsclient.services.Rule;

/**
 *
 * @author Victor
 */
public class Counting {
    private Map<Severity, Map<Rule, Integer>> severityCounts=new HashMap<>();
    private double rulesCompliance;
    
    public int getCount(Severity severity) {
        Map<Rule, Integer> map = severityCounts.get(severity);
        if(map == null) {
            return 0;
        }else{
            int sum=0;
            for(Integer i:map.values()) {
                sum+=i;
            }
            return sum;
        }
    }

    public double getRulesCompliance() {
        return rulesCompliance;
    }

    public void setRulesCompliance(double rulesCompliance) {
        this.rulesCompliance = rulesCompliance;
    }
    
    public Map<Rule, Integer> getRuleCounts(Severity severity) {
        if(severityCounts.containsKey(severity)) {
            return severityCounts.get(severity);
        }else{
            return new HashMap<>();
        }
    }

    public void setRuleCounts(Severity severity, Map<Rule, Integer> counts) {
        severityCounts.put(severity, counts);
    }
    
}
