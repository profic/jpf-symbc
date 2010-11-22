//
//Copyright (C) 2006 United States Government as represented by the
//Administrator of the National Aeronautics and Space Administration
//(NASA).  All Rights Reserved.
//
//This software is distributed under the NASA Open Source Agreement
//(NOSA), version 1.3.  The NOSA has been approved by the Open Source
//Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
//directory tree for the complete NOSA document.
//
//THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
//KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
//LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
//SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
//A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
//THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
//DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//

package gov.nasa.jpf.symbc.numeric;

public class MixedConstraint extends Constraint {
    public MixedConstraint(RealExpression l, Comparator c, IntegerExpression r) {
      super(l, c, r);
    }

    public MixedConstraint(MixedConstraint mc) {
        super(mc.getLeft(), mc.getComparator(), mc.getRight());
      }
    
    public String toString() {
      return /* "%Mixed% " + */super.toString();
    }
    
    public RealExpression getLeft() {
        return (RealExpression) super.getLeft();
    }
    
    public IntegerExpression getRight() {
        return (IntegerExpression) super.getRight();
    }
    
    @Override
    public MixedConstraint not() {
        return new MixedConstraint(getLeft(), getComparator().not(), getRight());
    }    
}
