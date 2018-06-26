# Simple LCG Parser


### Intro

This is an implementation of a chart parser for bounded-order Lambek Categorial Grammar based on (Fowler, 2016).
It uses term graphs, a variant of proof nets, as its syntactic formalism. As compared to the original parser 
described in the thesis, this implementation is radically simplified and less efficient. 


### Basic notions



### Definitions, data structures and algorithms described in (Fowler 2016)

I used basic defintions and data structures of term graphs, proof frame and syntactic categories decomposition, 
axiomatic linkage, L*- and L-integrity. Note that, contrary to the Lambek calculus tradition, syntactic categories are 
written using Steedman's notation and not that of Ajdukiewicz (i.e. we have reductions of the sort `B, A\B -> A` 
rather than `B, B\A -> A`). This is to comply with the term graph definition given in (Fowler 2016). However, 
converting between the two notations is trivial, so I leave it as an exercise to the user.     

For better understanding of the context I give here a brief comparison of the main notions used in the parser 
definitions with those used in the "parsing as deduction" literature.

| LCG Parser                                             |   Parsing as deduction                 |
|--------------------------------------------------------|----------------------------------------|
| term graph, proof net, proof frame`*` with linkage     |   parse tree                           |
| abstract proof frame (APF)`**`                         |   --                                   |
| partial term graph (PTG)                               |   subtree                              |
| abstract term graph (ATG)`***`                         |   item, e.g. `[A, i, j]`               |
| span (a sublist of atomic syntactic categories)        |   span (a substring between i and j)   |
| chart                                                  |   chart                                |

Notes:

`*`    a proof frame is precomputed and shared between all data structures, it is never modified
`**`   arguably, an Auxiliary Proof Frame would be a better name
`***`  arguably, an Auxiliary Partial Proof Net (APPN) would be a better name
`#`    "concise representative ATG for a PTG" simply means "a minimized auxiliary partial proof net satisfying
 well-formedness conditions"


### Correctness

* Unit tests with examples from Fowler's thesis. Maximum granularity, but coverage is limited.
* Implementing an alternative exponential-time parser (which is much simpler) and validating the final
  polynomial-time parser implementation to have approximate correctness guarantees. The downside is that
  this procedure is only applicable to the results and cannot determine where the problem occurs. Also, validation
  against a large dataset is impractical.


### Implementation details

Since in the deterministic part of the algorithm all possible planar linkages are built and thus up to that point
the whole procedure is deterministic, the logical properties of the system are explicated solely in the linkage validator.
During implementation I found a number of places in the Fowler's thesis which puzzled me, and I struggled somewhat
to make it work the way I would expect a regular categorial grammar to work. In the end I decided that an important
element was missing from Fowler's definition of an L-integral term graph. Namely, in the `T(CT)` part I had to add an
explicit check that the t node be reachable from x, which in my opinion complies with the definition of correctness
conditions for LG-graphs by (Penn 2004). Apart from that I also prohibit one-step regular paths between sources and
targets of lambek edges where the source has an incoming regular edge in the frame, which effectively rules out
the derivations violating a side-condition of a non-empty antecedent in the right-hand side introduction rules.
The resulting system is a product-free `L` without empty premises (although I give here no formal proofs of that).

The chart parser is the main implementation in this package. However, the name is somewhat misleading, since
the chart is only used to build planar matchings in the upper half of the term graph. Moreover, this procedure leads 
to spurious ambiguity, so the result set is deduplicated prior to final term graph validation. The resulting 
parser is nonetheless quite efficient and much simpler than the original parser described in (Fowler 2014).   


### Errata

* In the `T(CT)` validation rule I had to add an additional requirement that `t` is regular reachable from `x`, 
  which seems compatible with the original LG-graphs definition from (Penn 2004). 
* On p. 77, Fig. 3.14(b) a link between N2 and S4 is not eligible, should not the first category of the antecedent
  rather be S1\(N2/N3) instead of S1/(N2/N3)?
* On p. 153 the categories of 'by' and of its parent in the trees are swapped.


### License

MIT.