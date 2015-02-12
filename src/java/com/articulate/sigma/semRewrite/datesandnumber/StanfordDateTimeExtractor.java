package com.articulate.sigma.semRewrite.datesandnumber;

/*
Copyright 2014-2015 IPsoft

Author: Nagaraj Bhat nagaraj.bhat@ipsoft.com
        Rashmi Rao

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program ; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston,
MA  02111-1307 USA 
*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.BeginIndexAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.EndIndexAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NormalizedNamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;

public class StanfordDateTimeExtractor {

	public static List<String> NUMERICAL_ENTITIES = new ArrayList<String>(Arrays.asList("DATE",
			"NUMBER", "DURATION", "TIME"));
	
	private List<String> dependencyList = new ArrayList<String>();

	/** ***************************************************************
	 */
	public List<String> getDependencyList() {
		return dependencyList;
	}

	/** ***************************************************************
	 */
	public List<Tokens> populateParserInfo(String inputSentence) {
		
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		Annotation annotation;
		annotation = new Annotation(inputSentence);

		pipeline.annotate(annotation);
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		int cnt = 1;
		List<Tokens> tokenList = new ArrayList<Tokens>();
		for (CoreMap sentence: sentences) {
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
				String namedEntity = token.get(NamedEntityTagAnnotation.class);       
				if (NUMERICAL_ENTITIES.contains(namedEntity)) {
					Tokens tokens = new Tokens();
					tokens.setId(cnt);
					tokens.setWord(token.get(TextAnnotation.class));
					tokens.setNer(token.get(NamedEntityTagAnnotation.class));
					tokens.setNormalizedNer(token.get(NormalizedNamedEntityTagAnnotation.class));
					tokens.setCharBegin(token.get(BeginIndexAnnotation.class));
					tokens.setCharEnd(token.get(EndIndexAnnotation.class));
					tokenList.add(tokens);
				}
				cnt++;
			}
			SemanticGraph dependencies = sentence.get(CollapsedDependenciesAnnotation.class);
			dependencyList = StringUtils.split(dependencies.toList(), "\n");
		}
		return tokenList;
	}
}