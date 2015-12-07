package an.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Assert;
import org.junit.Test;


class Entity{

	List<Child> children = new ArrayList<Child>();
	String name;
	
	public boolean equals(Object other){
		return new EqualsBuilder()
        .append(name, ((Entity)other).name)
        .append(children, ((Entity)other).children)
        .isEquals();
	}
}

class Child extends Entity{
	List<Coach> coaches = new ArrayList<Coach>();
	String name;
	
	public boolean equals(Object other){
		return new EqualsBuilder()
        .append(name, ((Child)other).name)
        .append(coaches, ((Child)other).coaches)
        .isEquals();
	}
}

class Coach extends Entity{
	String name;

	public boolean equals(Object other){
		return new EqualsBuilder()
        .appendSuper(super.equals(other))
        .append(name, ((Coach)other).name)
        .isEquals();
	}	
}

public class NestedEqualsTest {

	@Test
	public void testEqual() throws Exception {
		Entity e1 = new Entity(), e2 = new Entity();
		createEntity(e1, new Child(), new Coach());
		createEntity(e2, new Child(), new Coach());
		
		Assert.assertTrue(e1.equals(e2));
		
		e1.children.get(0).coaches.get(0).name = "noname";
		
		Assert.assertFalse(e1.equals(e2));
	}

	private void createEntity(Entity e, Child c, Coach coach) {
		
		e.name = "e";
		c.name="child";
		c.coaches.add(coach);
		e.children.add(c);
	}
}
