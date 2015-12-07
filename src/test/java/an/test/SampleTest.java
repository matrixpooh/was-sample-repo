package an.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import an.rules.Person;

import com.sctrcd.drools.util.Activation;
import com.sctrcd.drools.util.TrackingAgendaEventListener;

public class SampleTest {

	StatefulKnowledgeSession ksession;

	@Before
	public void setup() {
		// load up the knowledge base
		KnowledgeBase kbase = readKnowledgeBase();

		// create the session
		ksession = kbase.newStatefulKnowledgeSession();
	}

	@After
	public void tearDown() {
		if (ksession != null)
			ksession.dispose();
	}

	public KnowledgeBase readKnowledgeBase() {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("an/test/rules.drl"),
				ResourceType.DRL);
		return kbuilder.newKnowledgeBase();
	}

	@Test
	public void testCountRules() throws Exception {
		Person bob = new Person("Bob", 35);
		ksession.insert(bob);

		int rulesFired = ksession.fireAllRules();

		Assert.assertEquals(2, rulesFired);
	}
	

	@Test
	public void testRulesFired() throws Exception {
		TrackingAgendaEventListener agendaEventListener = new TrackingAgendaEventListener();
		ksession.addEventListener(agendaEventListener);
//		ksession.addEventListener( new DebugAgendaEventListener() );
//		ksession.addEventListener( new DebugWorkingMemoryEventListener() );

		Person bob = new Person("Bob", 35);
		ksession.insert(bob);

		ksession.fireAllRules(10);

		List<Activation> activations = agendaEventListener.getActivationList();

		System.out.println("Rules that were fired:");
		for (Activation activation : activations) {
			System.out.println(activation.getRuleName());
		}

		assertThat(activations,
				hasItem(Matchers.<Activation>hasProperty("ruleName", org.hamcrest.Matchers.is("Name is Bob"))));
		
		assertTrue(agendaEventListener.isRuleFired("Person is 35 years old"));
		
		Map<String, Set<String>> keyRules = agendaEventListener.getKeyRule();
		for (String key : keyRules.keySet()) {
			System.out.print("KEY:" + key + "->");
			for (String string : keyRules.get(key)) {
				System.out.print(string +", ");
			}
			System.out.println();
		}
	}
	
	

}
