/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.spring.rest.mongo.dao;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import br.spring.rest.mongo.exceptions.MongoDBException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import com.mongodb.client.result.UpdateResult;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

/**
 *
 * @author Renan
 * @param <Entidade>
 */
public abstract class MongoDB<Entidade> implements IMongoDao<Entidade> {

    // objeto de acesso aos métodos do driver mongodb
    private MongoCollection<Document> db;

    private final Class<Entidade> tipoGenerico;

    private final Gson gson;

    public MongoDB(Class<Entidade> tipoGenerico) {
        this.tipoGenerico = tipoGenerico;
        gson = new Gson();

        MongoCollection<Document> collection = MongoConnection.getInstance().getDB().getCollection(tipoGenerico.getName());

        this.db = collection;
    }

    protected Class<Entidade> getTipoGenerico() {
        if (tipoGenerico != null) {
            return tipoGenerico;
        }
        Type genericSuperClass = getClass().getGenericSuperclass();

        ParameterizedType parametrizedType = null;

        while (parametrizedType == null) {
            if ((genericSuperClass instanceof ParameterizedType)) {
                parametrizedType = (ParameterizedType) genericSuperClass;
            } else {
                genericSuperClass = ((Class<?>) genericSuperClass).getGenericSuperclass();
            }
        }

        return (Class<Entidade>) parametrizedType.getActualTypeArguments()[0];

    }

    @Override
    public void onExcluir(Entidade entidade) {
        ObjectId idFromEntidade = getIdFromEntidade(entidade);
        BasicDBObject dataObject = new BasicDBObject();
        dataObject.put("_id", idFromEntidade);
        db.deleteOne(dataObject);
    }

    @Override
    public void onSalvar(Entidade object) throws MongoDBException {
        org.bson.Document convertObjectToDocument = convertObjectToDocument(object);

        //pegando id do document
        ObjectId id = getIdFromDocument(convertObjectToDocument);

        //encapsulado a edição
        if (id != null) {
            onAtualizar(object);
        } else {
            db.insertOne(convertObjectToDocument);

        }

    }

    @Override
    public void onAtualizar(Entidade object) throws MongoDBException {

        org.bson.Document convertObjectToDocument = convertObjectToDocument(object);

        //pegar id da entidade
        ObjectId id = getIdFromEntidade(object);

        //remove, ip imutável
        convertObjectToDocument.remove("_id");

        //condição where _id = id
        Bson condicao = new BasicDBObject("_id", id);
        Document documentoUpdate = new Document("$set", convertObjectToDocument);

        UpdateResult result = db.updateOne(condicao, documentoUpdate);
        if (result.getMatchedCount() <= 0) {
            throw new MongoDBException("Nenhum registro foi atualizado");
        }
    }

    @Override
    public List< Entidade> findAll() {

        List<Entidade> retorno = new ArrayList<>();

        FindIterable<Document> consulta = db.find();
        MongoCursor<Document> iterator = consulta.iterator();
        while (iterator.hasNext()) {
            Document documento = iterator.next();
            Entidade objeto = gson.fromJson(gson.toJson(documento), getTipoGenerico());

            retorno.add(objeto);
        }

        return retorno;
    }

    @Override
    public List<Entidade> buscaPaginada(int pagina, int tamanho, Entidade ultimaEntidade, Map<String, Object> filtrosOperadorAND, Map<String, Object[]> filtrosOperadorOR, String sortField, boolean ascendingOrder) {
        List<Entidade> retorno = new ArrayList<>();

        int ordenacao = 1; // ascendente
        String colunaOrdenacao = "id"; //ordena pela coluna

        if (sortField != null && !sortField.isEmpty()) { //caso tenha parametros troca as informações
            colunaOrdenacao = sortField;
            if (!ascendingOrder) {
                ordenacao = -1;
            }
        }

        boolean ordenacaoPorID = colunaOrdenacao.equals("id");

        BasicDBList convertFiltrosAnd = convertFiltrosAnd(filtrosOperadorAND);

        BasicDBObject filtrosAnd = new BasicDBObject();

        //se passou a ultima entidade e não for a primeira pagina executa o metodo
        // ele define a partir de qual a campo e posição que deverá começar a busca
        if (ultimaEntidade != null && pagina > 0) {
            Map objectToMap = objectToMap(ultimaEntidade);

            Object valorParaOrdenacao = null;

            if (ordenacaoPorID) {
                valorParaOrdenacao = mapToObject((LinkedHashMap) objectToMap.get(colunaOrdenacao), ObjectId.class);
                colunaOrdenacao = "_" + colunaOrdenacao; //se for id tem que colocar _ para filtrar corretamente
                //valorParaOrdenacao = getIdFromEntidade(ultimaEntidade);
            } else {
                valorParaOrdenacao = objectToMap.get(colunaOrdenacao);
            }

            System.out.println("Coluna: " + colunaOrdenacao);
            System.out.println("Valor: " + valorParaOrdenacao);
            System.out.println("Ordenação: " + ascendingOrder);

            BasicDBObject tipo;
            if (ascendingOrder) {
                //dependendo da ordenação precisa definir 
                //menor ou maior
                tipo = new BasicDBObject("$gt", valorParaOrdenacao);
            } else {
                tipo = new BasicDBObject("$lt", valorParaOrdenacao);
            }

            BasicDBObject coluna = new BasicDBObject(colunaOrdenacao, tipo);
            convertFiltrosAnd.add(coluna);
//            if (id != null) {
//                filtrosAnd.put(sortField, objectToMap.get(sortField));
//            }
        }

        if (!convertFiltrosAnd.isEmpty()) { // só adiciona se existir filtros
            filtrosAnd.put("$and", convertFiltrosAnd);
        }

        BasicDBObject orderBy = new BasicDBObject();
        orderBy.put(colunaOrdenacao, ordenacao);

        FindIterable<Document> consulta = db.find(filtrosAnd).limit(tamanho).sort(orderBy);

        MongoCursor<Document> cursor = consulta.iterator();

        while (cursor.hasNext()) {
            Document documento = cursor.next();
            Entidade objeto = gson.fromJson(gson.toJson(documento), getTipoGenerico());
            retorno.add(objeto);
        }

        return retorno;
    }

    @Override
    public int countLinhas(Map<String, Object> filtrosOperadorAND, Map<String, Object[]> filtrosOperadorOR) {

        BasicDBList convertFiltrosAnd = convertFiltrosAnd(filtrosOperadorAND);
        BasicDBObject filtrosAnd = new BasicDBObject();

        if (!convertFiltrosAnd.isEmpty()) { // só adiciona se existir filtros
            filtrosAnd.put("$and", convertFiltrosAnd);
        }

        Long countDocuments = db.countDocuments(filtrosAnd);

        return countDocuments.intValue();
    }

    private BasicDBList convertFiltrosAnd(Map<String, Object> filtrosOperadorAND) {
        BasicDBList andList = new BasicDBList();
//        BasicDBObject query = new BasicDBObject();
        if (filtrosOperadorAND == null || filtrosOperadorAND.isEmpty()) {
            return andList;
        }

        Iterator<Map.Entry<String, Object>> it = filtrosOperadorAND.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> pair = it.next();
            BasicDBObject tipo = null;
            BasicDBObject coluna = null;

            if (!pair.getKey().equals("proximosID")) {
                if (pair.getValue() instanceof String) {
                    tipo = new BasicDBObject("$regex", Pattern.compile(".*" + pair.getValue() + ".*", Pattern.CASE_INSENSITIVE));
                } else {
                    System.out.println(pair.getKey() + " - " + pair.getValue());
                    tipo = new BasicDBObject("$eq", pair.getValue());
                }
                coluna = new BasicDBObject(pair.getKey(), tipo);
            } else {
                tipo = new BasicDBObject("$gt", pair.getValue());
                coluna = new BasicDBObject("_id", tipo);
            }

            andList.add(coluna);
        }

        return andList;
//        query.put("$and", andList);
//
//        return query;
    }

    @Override
    public Entidade buscaUnica(Map<String, Object> filtrosOperadorAND, Map<String, Object[]> filtrosOperadorOR) {

        BasicDBList convertFiltrosAnd = convertFiltrosAnd(filtrosOperadorAND);
        BasicDBObject filtrosAnd = new BasicDBObject();

        if (!convertFiltrosAnd.isEmpty()) { // só adiciona se existir filtros
            filtrosAnd.put("$and", convertFiltrosAnd);
        }

        FindIterable<Document> consulta = db.find(filtrosAnd).limit(1);
        MongoCursor<Document> cursor = consulta.iterator();
        if (cursor.hasNext()) {
            Document documento = cursor.next();
            Entidade objeto = gson.fromJson(gson.toJson(documento), getTipoGenerico());
            return objeto;
        }
        return null;
    }

    public MongoCollection<Document> getDb() {
        return db;
    }

    public void setDb(MongoCollection<Document> db) {
        this.db = db;
    }

    public org.bson.Document convertObjectToDocument(Object obj) throws MongoDBException {
        Document document = Document.parse(gson.toJson(obj));
        return document;
    }

    //METODOS ESTATICOS UTILS
    public static ObjectId getIdFromEntidade(Object entidade) {
        Map objectToMap = objectToMap(entidade);

        LinkedHashMap idObject = (LinkedHashMap) objectToMap.get("id");

        //conversao de map para object
        ObjectId id = (ObjectId) mapToObject(idObject, ObjectId.class);

        return id;
    }

    public static ObjectId getIdFromDocument(org.bson.Document document) {
        Gson gson = new Gson();
        ObjectId retorno = null;
        //conversao de map para object
        Object idObject = document.get("_id");
        if (idObject != null) {
            retorno = gson.fromJson(gson.toJson(idObject), ObjectId.class);
        }

        return retorno;
    }

    public static Object mapToObject(Map map, Class clazz) {
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(map);
        Object retorno = gson.fromJson(jsonElement, clazz);
        return retorno;
    }

    public static Map objectToMap(Object object) {
        Map<String, Object> map = new HashMap<>();
        try {
            ObjectMapper oMapper = new ObjectMapper();
            map = oMapper.convertValue(object, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    //não utilizado
    private Entidade mapToObject(Map map) {
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(map);
        Entidade retorno = gson.fromJson(jsonElement, getTipoGenerico());
        return retorno;
    }

}
