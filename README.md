# GLLParsing
Questa repository contiene vari algortimi a riguardo del GLL Parsing. Il GLL Parsing è un parsing generalizzato top-down che utilizza il non determinismo 
per superare i conflitti imposti dal parsing LL(1). Infatti ogni qualvolta si presenta un conflitto viene dupilcato lo stack di esecuzione.
La computazione di questo algoritmo crea una struttura dati che si chiama: <b> Graph Structured Stack </b>. Questa struttura è un grafo diretto aciclico
che contiene nodi etichettati con l'identificativo dello stack e i simboli della grammatica. Gli algoritmi descritti si riferiscono alla seguente grammatica:

+ <b> S->  ASd | BS | epsilon
+ A->  a | c 
+ B->  a | b </b>
