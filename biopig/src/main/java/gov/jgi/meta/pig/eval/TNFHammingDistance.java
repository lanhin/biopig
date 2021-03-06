package gov.jgi.meta.pig.eval;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.hadoop.io.Text;
import org.apache.pig.EvalFunc;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.DataBag;
import org.apache.pig.data.DataType;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.apache.pig.impl.logicalLayer.FrontendException;
import org.apache.pig.impl.logicalLayer.schema.Schema;

public class TNFHammingDistance extends EvalFunc<Tuple> {

	@Override
	public Tuple exec(Tuple input) throws IOException {
		Tuple values = (Tuple) input.get(0);

		if (values.size() == 0)
			return null;

		Tuple t = TupleFactory.getInstance().newTuple(1);
		t.set(0, calculateDistance(values));

		return t;
	}

	private int calculateDistance(Tuple values) throws ExecException {

		DataBag value0 = (DataBag) values.get(0);
		DataBag value1 = (DataBag) values.get(1);
		HashMap<String, Long> map0 = new HashMap();
		Set<String> kmers = new HashSet();
		for (Iterator<Tuple> it = value0.iterator(); it.hasNext();) {
			Tuple t = it.next();
			String a = (String) t.get(0);
			Long b = (Long) t.get(1);
			map0.put(a, b);
			kmers.add(a);
		}
		HashMap<String, Long> map1 = new HashMap();
		for (Iterator<Tuple> it = value1.iterator(); it.hasNext();) {
			Tuple t = it.next();
			String a = (String) t.get(0);
			Long b = (Long) t.get(1);
			map1.put(a, b);
			kmers.add(a);
		}

	
		int dis = 0;
		for (Iterator<String> it = kmers.iterator(); it.hasNext();) {
			String key = it.next();
			if (!(map0.containsKey(key) && map1.containsKey(key))) {
				dis++;
			}
		}
		return dis;
	}

	@Override
	public Schema outputSchema(Schema input) {
		try {
			Schema.FieldSchema tokenFs = new Schema.FieldSchema("distance",
					DataType.INTEGER);
			Schema tupleSchema = new Schema(tokenFs);

			Schema.FieldSchema tupleFs;
			tupleFs = new Schema.FieldSchema("tuple_of_distance", tupleSchema,
					DataType.TUPLE);
			return (new Schema(tupleFs));
		} catch (FrontendException e) {
			// throwing RTE because
			// above schema creation is not expected to throw an exception
			// and also because superclass does not throw exception
			throw new RuntimeException("Unable to compute TOKENIZE schema.");
		}
	}
}
