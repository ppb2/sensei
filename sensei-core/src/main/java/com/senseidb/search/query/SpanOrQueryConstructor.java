package com.senseidb.search.query;

import java.util.ArrayList;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SpanOrQueryConstructor extends QueryConstructor {

  public static final String QUERY_TYPE = "span_or";

  @Override
  protected Query doConstructQuery(JSONObject jsonQuery) throws JSONException {

    // "span_or" : {
    // "clauses" : [
    // { "span_term" : { "field" : "value1" } },
    // { "span_term" : { "field" : "value2" } },
    // { "span_term" : { "field" : "value3" } }
    // ]
    // },

    String fieldCheck = null;
    JSONArray jsonArray = jsonQuery.getJSONArray(CLAUSES_PARAM);
    ArrayList<SpanTermQuery> clausesList = new ArrayList<SpanTermQuery>();
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject json = jsonArray.getJSONObject(i).getJSONObject(SPAN_TERM_PARAM);
      String field = (String) (json.keys().next());

      if (fieldCheck == null) fieldCheck = field;
      else if (!fieldCheck.equals(field)) throw new IllegalArgumentException(
          "Clauses must have same field: " + jsonQuery);

      String value = (String) json.get(field);
      clausesList.add(new SpanTermQuery(new Term(field, value)));
    }

    SpanQuery[] clauses = clausesList.toArray(new SpanQuery[clausesList.size()]);
    SpanOrQuery soq = new SpanOrQuery(clauses);
    return soq;
  }
}
