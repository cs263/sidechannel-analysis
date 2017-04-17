# Sidechannel Analysis

## Project Vision

We will be doing an evaluation of side-channels in runtime systems. Side-channels are present when information about the internal workings of a system can be inferred from measuring certain, often seemingly irrelevant, properties. These properties are usually inherently tied to either the hardware or the runtime, such as the execution time of the program or amount of memory used. We will be implementing ten programs with runtime-specific side channel vulnerabilities on different managed runtimes and analyzing the results. We will quantify any differences in side-channel strength depending on the language and runtime. We will estimate and validate which language or runtime traits caused the differences to occur, and actively search for a way to patch these, if possible.

## Side-channel Overview
 A practically useful system always presents a complicated landscape, in an
 information-theoretical view: to be deemed ``useful", computations have certain
 characteristic behaviours they should follow (e.g. be of a certain complexity
 class, execute at a certain speed, etc.), which more or less exerts certain
 properties of the physical implementation of the system in question. By not
 focusing on the result of the computation, but rather on the side-effects
 manifest in this physical implementation, one can gain knowledge of some
 implementational details, and thus the structure of the computation itself. This
 is referred to as using a side-channel to learn things. Similarly, we say that
 looking at the computation directly is a main-channel.
 
 Side-channel exploration and exploitation have mostly been a part of the realm
 of cryptography, but with the coming of Web 2.0 and mobile technologies, as well
 as the exponential increase in processing power compared to power output or
 computation time required, the possibility of finding a side-channel grew
 significantly. One of the foci of side-channel analyses is preventing side-
 channel attacks, in which an attacker is using one or potentially more side-
 channels to discover some private data or gain insight into a hidden process.
 That being as important as it is, side-stepping security, one (friendly
 individual) can learn a lot about a system by observing side-channels. For
 example,  timing side-channels present a view into different execution times of
 a program, most frequently dependent only on the input passed into it. If the
 program isn't observably polynomial with the size of the inputs (which would be
 a trivial side-channel to catch), but we do see a pattern occuring with
 differing inputs, we can conclude that there exists a branch in the program
 structure which uses the input as a choice point: a certain class of inputs
 activates one path in that branch, leading to some execution time $t_1$, while
 the other leads to an observably different execution time $t_2$. This, for
 example, reflects a very common coding practice that is used widely in
 imperative programming, namely: early returns. In many environemnts where early
 returns are predominant,  programmers value optimizing for speed, but such
 optimizations could never cover the space of all programs for which they are
 implemented, for all inputs\footnote{If an optimization could ever cover the
 space of all programs for which it is implemented, for all inputs, that means
 that it  would collapse the whole complexity class in which it is by one and
 lead to the side-channel  itself not being recognizable.}, making it possible
 for analyses to get knowledge about the system under test without breaking the
 seal on the black-box.
 
 This phenomenon builds a kind of philosophical construct similar to the Heisenberg
 uncertainty principle wherein the limitations of our physical reality are stopping
 us from being both optimized (take less time in most cases) and, for example,
 completely computationally private (as observed in \cite{archlab-timing-14}). The
 case in which the least side-channel activity is present is the case in which the
 lengths of all the possible routes of execution of the main-channel are of similar
 size, and thus observation becomes noisy in the presence of other physical factors.
 This, unsurprisingly, is the case in which there is no optimization made and all the
 lengths are similar to the longest one.


## Team

- Tegan Brennan (8133175)
- Miroslav Gavrilov (9379132)

## Resources

- [On Code Execution Tracking via Power Side-Channel](http://dl.acm.org/citation.cfm?id=2978299)
- [Thwarting Cache Side-Channel Attacks Through Dynamic Software Diversity](https://www.ics.uci.edu/~ahomescu/ndss15sidechannels.pdf)
- [A Note On Side-Channels Resulting From Dynamic Compilation](https://eprint.iacr.org/2006/349.pdf)
- [Compiler Mitigations for Time Attacks on Modern x86 Processors](https://pdfs.semanticscholar.org/5727/7ff4c38a86d84a8fb7eb09625d5a2c545f7c.pdf)
- [MSC06-CPP. Be aware of compiler optimization when dealing with sensitive data](https://www.securecoding.cert.org/confluence/display/cplusplus/MSC06-CPP.+Be+aware+of+compiler+optimization+when+dealing+with+sensitive+data)
