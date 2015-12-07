package com.sctrcd.drools.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.definition.rule.Rule;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.DefaultAgendaEventListener;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import an.rules.Person;

/**
 * A listener that will track all rule firings in a session.
 * 
 * @author Stephen Masters
 */
public class TrackingAgendaEventListener extends DefaultAgendaEventListener {

    private static Logger log = LoggerFactory.getLogger(TrackingAgendaEventListener.class);

    private List<Activation> activationList = new ArrayList<Activation>();
    
    private final Map<String, Set<String>> keyRule = new HashMap<String, Set<String>>();

    @Override
    public void afterActivationFired(AfterActivationFiredEvent event) {
        Rule rule = event.getActivation().getRule();

        String ruleName = rule.getName();
        Map<String, Object> ruleMetaDataMap = rule.getMetaData();

        activationList.add(new Activation(ruleName));
        StringBuilder sb = new StringBuilder("Rule fired: " + ruleName);

        if (ruleMetaDataMap.size() > 0) {
            sb.append("\n  With [" + ruleMetaDataMap.size() + "] meta-data:");
            for (String key : ruleMetaDataMap.keySet()) {
                sb.append("\n    key=" + key + ", value="
                        + ruleMetaDataMap.get(key));
            }
        }

        System.out.println(sb.toString());
        
		KnowledgeRuntime knowledgeRuntime = event.getKnowledgeRuntime();
		ObjectFilter filter = new ObjectFilter() {
			
			public boolean accept(Object object) {
				return object.getClass() == Person.class;
			}
		};
		knowledgeRuntime.getFactHandles(filter);
		for (FactHandle factHandle : event.getActivation().getFactHandles()) {
			Person fact = (Person)knowledgeRuntime.getObject(factHandle);
			
			Set<String> rules = keyRule.get(fact.getName());
			if (rules == null)
				rules = new HashSet<String>();
			rules.add(ruleName);
			keyRule.put(fact.getName(), rules);
		}
    }

    public boolean isRuleFired(String ruleName) {
        for (Activation a : activationList) {
            if (a.getRuleName().equals(ruleName)) {
                return true;
            }
        }
        return false;
    }

    public void reset() {
        activationList.clear();
    }

    public final List<Activation> getActivationList() {
        return activationList;
    }

    public final Map<String, Set<String>> getKeyRule(){
    	return keyRule;
    }
    
    public String activationsToString() {
        if (activationList.size() == 0) {
            return "No activations occurred.";
        } else {
            StringBuilder sb = new StringBuilder("Activations: ");
            for (Activation activation : activationList) {
                sb.append("\n  rule: ").append(activation.getRuleName());
            }
            return sb.toString();
        }
    }

}
