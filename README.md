# Sidechannel Analysis

## Project Vision

We will be doing an evaluation of side-channels in runtime systems. Side-channels are present when information about the internal workings of a system can be inferred from measuring certain, often seemingly irrelevant, properties. These properties are usually inherently tied to either the hardware or the runtime, such as the execution time of the program or amount of memory used. We will be implementing ten programs with runtime-specific side channel vulnerabilities on different managed runtimes and analyzing the results. We will quantify any differences in side-channel strength depending on the language and runtime. We will estimate and validate which language or runtime traits caused the differences to occur, and actively search for a way to patch these, if possible.

## Team

- Tegan Brennan (8133175)
- Miroslav Gavrilov (9379132)

## Resources

- [On Code Execution Tracking via Power Side-Channel](http://dl.acm.org/citation.cfm?id=2978299)
- [Thwarting Cache Side-Channel Attacks Through Dynamic Software Diversity](https://www.ics.uci.edu/~ahomescu/ndss15sidechannels.pdf)
- [A Note On Side-Channels Resulting From Dynamic Compilation](https://eprint.iacr.org/2006/349.pdf)
- [Compiler Mitigations for Time Attacks on Modern x86 Processors](https://pdfs.semanticscholar.org/5727/7ff4c38a86d84a8fb7eb09625d5a2c545f7c.pdf)
- [MSC06-CPP. Be aware of compiler optimization when dealing with sensitive data](https://www.securecoding.cert.org/confluence/display/cplusplus/MSC06-CPP.+Be+aware+of+compiler+optimization+when+dealing+with+sensitive+data)
- [JVM versus CLR: A Comparative Study](https://pdfs.semanticscholar.org/b57a/af581e043fb63c56ebd662720190e3121220.pdf)
## Tools Used

- For build duties, we're testing out an in-house tool called `gyros`, which does automatic background rebuilds guided by recipes and tries to do a generally better job than Makefiles (source code not included because it would be cheating, but available [here](https://github.com/gavrilovmiroslav/gyros).

