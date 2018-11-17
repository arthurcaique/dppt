/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stanfordUtils;

import com.acbv.propagacao_dupla.utils.TreeTaggerUtils;
import stanfordUtils.entidades.FraseStfd;
import stanfordUtils.entidades.TokenStfd;
import stanfordUtils.entidades.TextoStfd;
import stanfordUtils.entidades.RelacaoDependenciaStfd;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Arthur
 */
public class StanfordMethods {

    protected MaxentTagger maxEntTagger;
    protected DependencyParser depParser;

    @SuppressWarnings("")
    public StanfordMethods() {
        config();
    }

    private void config() {
        if (this.maxEntTagger == null) {
            this.maxEntTagger = (MaxentTagger) MaxentTagger.loadModel("/home/arthur/Downloads/pt-model/pos-tagger.dat");
        }
        if (this.depParser == null) {
            this.depParser = DependencyParser.loadFromModelFile("/home/arthur/Downloads/pt-model/dep-parser");
        }
    }

    public TextoStfd[] getTextos(String[] corpus) throws Exception {
        int corpusLength = corpus.length;
        TextoStfd[] textos = new TextoStfd[corpusLength];
        TextoStfd textoStfd;
        for (int i = 0; i < corpusLength; i++) {
            textoStfd = this.parseText(corpus[i]);
            textos[i] = textoStfd;
        }
        
        return textos;
    }

    public TextoStfd parseText(String texto) throws Exception {
        FraseStfd[] frases = getFrases(texto);
        TextoStfd textoStfd = new TextoStfd(texto, frases);
        return textoStfd;
    }

    private FraseStfd[] getFrases(String texto) throws Exception {
        List<List<HasWord>> tokenizedText = MaxentTagger.tokenizeText(new StringReader(texto));
        FraseStfd frase;
        List<FraseStfd> frasesList = new ArrayList<>();
        for (List<HasWord> hwSentence : tokenizedText) {
            List<TaggedWord> taggedSentence = (ArrayList<TaggedWord>) this.maxEntTagger.tagSentence(hwSentence);
            taggedSentence = preProccess(taggedSentence);
            List<TokenStfd> tokensFrase = new ArrayList<>();
            String fraseStr = "";
            for (TaggedWord taggedWord : taggedSentence) {
                String tokenStr = taggedWord.word();
                String tag = taggedWord.tag();
                TokenStfd token = new TokenStfd(tokenStr, tag);
                if (fraseStr.isEmpty()) {
                    fraseStr = token.getToken();
                } else {
                    fraseStr = fraseStr.concat(" " + token.getToken());
                }
                tokensFrase.add(token);
            }
            RelacaoDependenciaStfd[] relacoesDependencia = toRelacaoDependencia(taggedSentence, tokensFrase);
            tokensFrase = setLemmaTokensFrase(tokensFrase);
            frase = new FraseStfd(fraseStr, relacoesDependencia);
            TokenStfd[] tokens = tokensFrase.toArray(new TokenStfd[tokensFrase.size()]);
            frase.setTokens(tokens);
            frasesList.add(frase);
        }
        FraseStfd[] frases = frasesList.toArray(new FraseStfd[frasesList.size()]);
        return frases;
    }

    private List<TaggedWord> preProccess(List<TaggedWord> tagged) {
        for (int i = 0; i < tagged.size(); i++) {
            TaggedWord taggedWord = tagged.get(i);
            String word = taggedWord.word();
            if (word.equalsIgnoreCase("-RRB-")
                    || word.equalsIgnoreCase("-LRB-")) {
                String tag = ".";
                word = word.equalsIgnoreCase("-RRB-") ? ")"
                        : word.equalsIgnoreCase("-LRB-") ? "("
                        : word;
                taggedWord.setFromString(word);
                taggedWord.setTag(tag);
                tagged.set(i, taggedWord);
            }
        }
        return tagged;
    }

    private RelacaoDependenciaStfd[] toRelacaoDependencia(List<TaggedWord> tagged, List<TokenStfd> tokens) throws Exception {
        List<TypedDependency> typedDependencies = getTypedDependencies(tagged);
        List<RelacaoDependenciaStfd> relacoesDependenciaList = new ArrayList<>();
        RelacaoDependenciaStfd relacao_dependencia;
        for (TypedDependency typedDependency : typedDependencies) {
            TokenStfd dependente = indexedWordToToken(typedDependency.dep(), tokens);
            TokenStfd governante = indexedWordToToken(typedDependency.gov(), tokens);
            String relacao = typedDependency.reln().toString();
//            String dependente_nodo = typedDependency.dep().word();
//            String dependente_categoria = typedDependency.dep().tag();
//            String governante_nodo = typedDependency.gov().word();
//            String governante_categoria = typedDependency.gov().tag();
//            TokenStfd governante = new TokenStfd(governante_nodo, governante_categoria);
//            TokenStfd dependente = new TokenStfd(dependente_nodo, dependente_categoria);

            relacao_dependencia = new RelacaoDependenciaStfd(relacao, governante, dependente);
            relacoesDependenciaList.add(relacao_dependencia);
        }
        RelacaoDependenciaStfd[] relacoesDependencia = relacoesDependenciaList.toArray(new RelacaoDependenciaStfd[relacoesDependenciaList.size()]);
        return relacoesDependencia;
    }

    private TokenStfd indexedWordToToken(IndexedWord indexedWord, List<TokenStfd> tokens) throws IOException, Exception {
        List<String> tokens2 = new ArrayList<>();
        tokens.forEach((token) -> {
            tokens2.add(token.getToken());
        });
        int index = indexedWord.index() - 1;
        String[] lemmas = TreeTaggerUtils.getTreeTaggerUtils().lematizar(tokens2);
        if (tokens2.size() != lemmas.length) {
            System.out.println("ERRO NO TAGGER EM INDEX WORD STANFORD NLP PARSING");
        }
        String tokenStr, posTagToken, lemma;
        if (!Objects.isNull(indexedWord)
                && Objects.isNull(indexedWord.word())
                && Objects.isNull(indexedWord.tag())) {
            tokenStr = "";
            posTagToken = "";
            lemma = "";
        } else {
            tokenStr = indexedWord.word();
            posTagToken = indexedWord.tag();
            lemma = lemmas[index];
        }
        TokenStfd te = new TokenStfd(tokenStr, posTagToken);
        te.setLemma(lemma);
        return te;
    }

    private List<TokenStfd> setLemmaTokensFrase(List<TokenStfd> tokensFrase) throws IOException, Exception {
        List<TokenStfd> tokens = new ArrayList<>();
        List<String> tokensStr = new ArrayList<>();
        tokensFrase.forEach((token) -> {
            tokensStr.add(token.getToken());
        });
        String[] lemmas = TreeTaggerUtils.getTreeTaggerUtils().lematizar(tokensStr);
        if (tokensFrase.size() != lemmas.length) {
            System.out.println("Erro em set lemmas tokens frase em StanfordNLPParsing");
        } else {
            for (int i = 0; i < tokensFrase.size(); i++) {
                TokenStfd t = tokensFrase.get(i);
                t.setLemma(lemmas[i]);
                tokens.add(t);
            }
        }
        return tokens;
    }

    private List<TypedDependency> getTypedDependencies(List<TaggedWord> tagged) {
        GrammaticalStructure gs = this.depParser.predict(tagged);
        Collection<TypedDependency> tdl = gs.typedDependenciesCollapsedTree();
        List<TypedDependency> td = new ArrayList<>(tdl);
        return td;
    }

//    private String getTag(String palavra, String tag, String texto, int sentenceIndex) {
//        texto = texto.replaceAll(palavra, palavra.toLowerCase());
//        List<List<HasWord>> tokenizedText = MaxentTagger.tokenizeText(new StringReader(texto));
//        List<HasWord> get = tokenizedText.get(sentenceIndex);
//        List<TaggedWord> taggedSentence = (ArrayList<TaggedWord>) this.maxEntTagger.tagSentence(get);
//        for (TaggedWord taggedWord : taggedSentence) {
//            if (taggedWord.word().equals(palavra.toLowerCase()) && !tag.equals(taggedWord.tag())) {
//                tag = taggedWord.tag();
//            }
//        }
//        return tag;
//    }
//    public boolean firstLetterIsTheOnlyCapitalized(String string) {
//        boolean is = false;
//        IntStream codePoints = string.codePoints();
//        int[] codePointsVctr = codePoints.toArray();
//        int numCodePoints = codePointsVctr.length;
//        if (numCodePoints > 0) {
//            boolean firstIs = Character.isUpperCase(codePointsVctr[0]);
//            if (firstIs) {
//                is = firstIs;
//                if (numCodePoints == 1) {
//                } else {
//                    for (int i = 1; i < numCodePoints; i++) {
//                        int codePoint = codePointsVctr[i];
//                        boolean anotherUpperCase = Character.isUpperCase(codePoint);
//                        if (anotherUpperCase == true) {
//                            is = false;
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//        return is;
//    }
//    public Tipo_Relacao getTipoRelacao(String relacao) {
//        relacao = relacao.toUpperCase();
//        Tipo_Relacao tipoRelacao = Tipo_Relacao.getTipoRelacao(relacao);
//        return tipoRelacao;
//    }
}
