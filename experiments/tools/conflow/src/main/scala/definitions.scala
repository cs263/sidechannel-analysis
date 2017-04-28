
/*

Reading material: http://doc.utwente.nl/79610/1/FoVeOOS11-PreProceeding.pdf

verifiable cfgs!

@tegan: they pack instructions into sets and build a control-
flow graph that is verified to be correct for the given
instructions. we could use this to create a model for
side-channel analysis and homology groups across languages.

this is very similar to what we were to do before, but
this time with a strong theoretical basis for why it's okay.

possible problem: they mostly care about exceptional execution
paths, which might be okay

*/

package conflow {
	trait InstructionSet

	trait ReturnInstructions extends InstructionSet
	trait CompareInstructions extends InstructionSet
	trait ConditionalInstructions extends InstructionSet
	trait JumpInstructions extends InstructionSet
	trait ExceptionInstructions extends InstructionSet
	trait InvocationInstructions extends InstructionSet
	trait ThrowInstructions extends InstructionSet

	// notes while reading paper:

	// Vm = set of control nodes for model m 
	// Lm = set of labels 
	// Am = { m, r } ∪ E, ∀v ∈ Vm . m ∈ λm(v)
	//					  ∀x, x' ∈ E . { x, x' } ⊆ λm(v) => x = x'

	// node with r = return node of the method

	// v |= x 	= node v is tagged with exception x
	// ∙l,x_m 	= an exceptional control node
	// ∘l_m		= normal control node

}