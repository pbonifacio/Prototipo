package com.app.plan2see.db.controller;

import com.app.plan2see.model.db.Term;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class TermsController extends DBController {
    
    List<Term> terms = new ArrayList<Term>();
    
    public TermsController(Session session){
        super(session);
    }
    
    @Override
    public Term getEntityById(int id) {
        Term term = (Term) session.get(Term.class, id);
        return term;
    }
    
    @Override
    public int getEntitiesCount(){
        int size = session.createQuery("from Term").list().size();
        return size;
    }
    
    @Override
    public List<Term> getAllEntities(){
        if( terms.size() == 0 ){
            terms = session.createQuery("from Term").list();
        }
        return terms;
    }
    
    @Override
    public Term getEntityByColumnFilter(String columnName, String columnValue) {
        Term term = (Term) session.createCriteria(Term.class)
             .add( Restrictions.eq(columnName, columnValue) )
                .setMaxResults(1).uniqueResult();
        return term;
    }
}
