package gov.nasa.jpf.symbc.numeric;

import gov.nasa.jpf.util.test.TestJPF;

import org.junit.Test;

public class TestPathCondition extends TestJPF {

	private Constraint[] initConstraints(int numberOfConstraints) {
		Constraint[] constraints = new Constraint[numberOfConstraints];
		IntegerConstant[] constants = new IntegerConstant[numberOfConstraints * 2];

		for (int i = 0; i < numberOfConstraints * 2; i++) {
			constants[i] = new IntegerConstant(i);
		}
		for (int i = 0; i < numberOfConstraints; i++) {
			constraints[i] = new LinearIntegerConstraint(constants[2 * i], Comparator.LE, constants[2 * i + 1]);
		}

		return constraints;
	}

	@Test
	public void appendAllConjunctsToEmptyResultConstraintShouldBeEqualToAppendedConstraints() {
		Constraint[] constraints = initConstraints(2);
		PathCondition pathCondition = new PathCondition();
		
		constraints[0].setNextConstraint(constraints[1]);
		pathCondition.appendAllConjuncts(constraints[0]);

		assertEquals(2, pathCondition.getCount());
		assertEquals(constraints[0], pathCondition.getHeader());
		assertEquals(constraints[1], pathCondition.last());
	}

	@Test
	public void appendAllConjunctsToNonEmptyConstraintsShouldBeCorrectlyPrepended() {
		Constraint[] constraints = initConstraints(3);
		PathCondition pathCondition = new PathCondition();
		
		constraints[1].setNextConstraint(constraints[2]);
		pathCondition.appendAllConjuncts(constraints[0]);
		pathCondition.appendAllConjuncts(constraints[1]);

		assertEquals(3, pathCondition.getCount());
		assertEquals(constraints[0], pathCondition.getHeader());
		assertEquals(constraints[1], pathCondition.getHeader().getNextConstraint());
		assertEquals(constraints[2], pathCondition.last());
	}

	@Test
	public void prependAllConjunctsToEmptyResultConstraintShouldBeEqualToPrependedConstraints() {
		Constraint[] constraints = initConstraints(3);
		PathCondition pathCondition = new PathCondition();
			
		constraints[0].setNextConstraint(constraints[1]);
		pathCondition.prependAllConjuncts(constraints[0]);

		assertEquals(2, pathCondition.getCount());
		assertEquals(constraints[0], pathCondition.getHeader());
		assertEquals(constraints[1], pathCondition.last());
	}

	@Test
	public void prependAllConjunctsToNonEmptyConstraintsShouldBeCorrectlyPrepended() {
		Constraint[] constraints = initConstraints(3);
		PathCondition pathCondition = new PathCondition();
		
		constraints[0].setNextConstraint(constraints[1]);
		pathCondition.appendAllConjuncts(constraints[2]);
		pathCondition.prependAllConjuncts(constraints[0]);

		assertEquals(3, pathCondition.getCount());
		assertEquals(constraints[0], pathCondition.getHeader());
		assertEquals(constraints[1], pathCondition.getHeader().getNextConstraint());
		assertEquals(constraints[2], pathCondition.last());
	}
	
	@Test
	public void prependPathConditionToEmptyResultConstraintShouldBeEqualToAddedConstraints() {
		Constraint[] constraints = initConstraints(2);
		PathCondition pathCondition1 = new PathCondition();
		PathCondition pathCondition2 = new PathCondition();
		
		constraints[0].setNextConstraint(constraints[1]);
		pathCondition2.appendAllConjuncts(constraints[0]);
		pathCondition1.prependPathCondition(pathCondition2);

		assertEquals(2, pathCondition1.getCount());
		assertEquals(constraints[1], pathCondition1.getHeader());
		assertEquals(constraints[0], pathCondition1.last());
	}
	
	@Test
	public void removeHeaderShouldRemoveHeaderFromPathCondition() {
		Constraint[] constraints = initConstraints(2);
		PathCondition pathCondition = new PathCondition();
		
		constraints[0].setNextConstraint(constraints[1]);
		pathCondition.appendAllConjuncts(constraints[0]);
		pathCondition.removeHeader();
		
		assertEquals(1, pathCondition.getCount());
		assertEquals(constraints[1], pathCondition.getHeader());
		assertEquals(constraints[1], pathCondition.last());
	}
}
