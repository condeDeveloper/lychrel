# lychrel

Pegue um número. Some com ele escrito de trás para frente. Repita.

```
57  →  57 + 75  = 132
132 → 132 + 231 = 363   palíndromo, em dois passos
```

Quase todo número chega a um palíndromo assim, e rápido. O **89** demora 24 passos e
termina em `8813200023188`. E o **196** nunca chegou: já foram bilhões de passos, centenas
de milhões de dígitos, e nada. Ninguém provou que ele nunca chega — por isso ele é
*candidato* a Lychrel, e não Lychrel.

Este repositório varre todos os números até um milhão e conta.

```bash
mvn -B -ntp verify                               # 14 testes
mvn -B -ntp exec:java -Dexec.args="varredura"    # até dez mil, com a distribuição
mvn -B -ntp exec:java -Dexec.args="recordes"     # quem bate recorde de passos
mvn -B -ntp exec:java -Dexec.args="candidatos"   # os que não chegam
mvn -B -ntp exec:java -Dexec.args="juiz"         # BigInteger contra long
mvn -B -ntp exec:java -Dexec.args="fundo"        # até um milhão (job próprio)
```

Java 21, sem dependência fora de JUnit e AssertJ.

## Por que BigInteger

A soma estoura depressa. Para o 196, o `long` aguenta exatamente **40 passos** antes de
não caber mais:

```
n        passo em que o long desiste
196      40
295      40
879      36
1997     31
```

Por isso a varredura inteira roda em `BigInteger`. Mas o caminho com `long` continua no
projeto, como juiz: enquanto ele aguenta, os dois precisam dar exatamente o mesmo número a
cada passo, e os dois testes de palíndromo precisam concordar.

```
passos conferidos nos dois caminhos, até n = 2.000: 6.302, todos iguais
```

O `long` não dá a volta em silêncio quando estoura — ele devolve `-1` e a varredura sabe
parar. Num problema cujo assunto *é* o crescimento do número, um estouro mudo entregaria
palíndromos que não existem.

## Quem demora

```
n      passos   dígitos do palíndromo
1      1        1
5      2        2
59     3        4
69     4        4
79     6        5
89     24       13
```

O salto entre os dois últimos é o melhor número deste repositório. O **79 resolve em 6
passos**. O **89, o vizinho dele, precisa de 24** — quatro vezes mais — e o palíndromo
final salta de 5 para 13 dígitos. Depois disso, nenhum número até dez mil bate esse
recorde.

## A distribuição

```
passos   quantos n
1        2838
2        2892
3        1356
4        926
...
13       70
14       4
15       83
...
24       2
nenhum   249
```

Duas coisas fogem do esperado. A primeira: **2 passos é mais comum que 1** — 2.892 contra
2.838. A segunda é mais estranha: **14 passos tem apenas 4 números, e 15 passos tem 83**,
vinte vezes mais. A curva não desce, ela serrilha.

## Os que não chegam

```
196  295  394  493  592  689  691  788  790  879  887  978  986
1495 1497 1585 1587 1675 1677 1765 1767 1855 1857 1945 1947 1997
```

São **249 até dez mil**, e o menor é o 196. Depois de duzentos passos ele já tem **88
dígitos** e continua não sendo palíndromo.

Repare na estrutura: `196, 295, 394, 493, 592` — os dígitos vão `1+9+6`, `2+9+5`, `3+9+4`,
sempre somando 16. Os candidatos chegam em famílias, não espalhados.

## Até um milhão

```
resolvem    884.035   (88,4035%)
candidatos  115.965   (11,5965%)
recorde     64 passos, em n = 150.296, palíndromo de 33 dígitos
```

A fatia de candidatos **sobe** com o intervalo: 2,49% até dez mil, 11,60% até um milhão. E
o recorde quase triplica, de 24 para 64 passos.

## O que as medidas me corrigiram

**O reverso de 10¹⁸ é 1.** Escrevi um teste afirmando que `10^18` estouraria o `long` ao
somar com o próprio reverso, e ele falhou: os zeros do fim viram zeros da **frente** e
somem, então o reverso é `1` e a soma é `1000000000000000001`, que cabe folgado. O código
estava certo; o teste é que supunha uma simetria que o número não tem. Hoje esse caso está
na bateria com o valor certo e um comentário dizendo por quê.

**Achei que a distribuição descesse suave.** Ela serrilha: 14 passos tem 4 números e 15
tem 83. E nem o começo é monótono — 2 passos é mais comum que 1.

**"Candidato" não é "é".** Nenhum dos 249 números desta lista foi provado Lychrel; eles
apenas não chegaram **dentro do meu teto de duzentos passos**. A primeira versão da tabela
dizia "números de Lychrel", o que é uma afirmação que ninguém no mundo pode fazer hoje. A
coluna diz `candidatos`, e o teto está escrito na tabela.

**O teto escolhe a resposta.** Com teto de 200 passos e até um milhão, 11,60% não chegam.
Esse número mede o teto tanto quanto mede os números — a mesma armadilha que o repositório
`aliquotas` tem documentada. A diferença é que aqui o teto é generoso: o 196 passa de 88
dígitos e nem assim se rende.

## Quatro achados

1. **O 79 resolve em 6 passos e o 89 precisa de 24** — quatro vezes mais, entre vizinhos —
   e o palíndromo final salta de 5 para 13 dígitos.
2. **249 candidatos até dez mil**, e eles vêm em famílias: `196, 295, 394, 493, 592` têm
   todos a mesma soma de dígitos, 16.
3. A distribuição de passos **serrilha em vez de descer**: 14 passos tem 4 números, 15 tem
   **83**, e 2 passos é mais comum que 1.
4. O `long` aguenta exatamente **40 passos** no caminho do 196 antes de estourar — e,
   enquanto aguenta, concorda com o `BigInteger` em **6.302 passos** conferidos um a um.
