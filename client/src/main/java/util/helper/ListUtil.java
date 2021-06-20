package util.helper;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;

import javafx.scene.transform.Transform;

public class ListUtil {

	public static List<Transform> reverse(List<Transform> transforms) {
		
		List<Transform> reversedOrderTransforms = new ArrayList<>();
		List<Transform> applied = new ArrayList<>();

		for(int i = (transforms.size()-1); i >= 0; i--) {
			reversedOrderTransforms.add(transforms.get(i));
		}
		
		return reversedOrderTransforms;
	}
	
	public static List<Transform> copy(List<Transform> transforms) {
		
		List<Transform> copy = new ArrayList<>();

		for(int i = 0; i < transforms.size(); i++) {
			copy.add(transforms.get(i));
		}
		
		return copy;
	}
	
	public static List<Transform> filter(List<Transform> transforms, Class<? extends Transform> clazz) {

		List<Class<? extends Transform>> clazzs = new ArrayList<>();
		clazzs.add(clazz);

		return filter(transforms, clazzs);

	}

	public static List<Transform> filter(List<Transform> transforms, List<Class<? extends Transform>> clazzs) {

		List<Transform> result = new ArrayList<>();

		for(Transform t : transforms) {
			for(Class c : clazzs) {
				if(!c.isInstance(t)) {
					result.add(t);
				}
			}
		}

		return result;

	}
	
	public static <T> T getLastAs(List<Node> nodes, Class<? extends T> clazz) {
		
		T lastChildren = null;
		int size = nodes.size();
		
		if(size > 0 && clazz.isInstance(nodes.get(size-1))) {
			
			lastChildren = (T) nodes.get(size-1);
		}
		
		return lastChildren;
	}
	
	public static <T> T getFirstAs(List<Node> nodes, Class<? extends T> clazz) {
		
		T firstChildren = null;
		int size = nodes.size();
		
		if(size > 0 && clazz.isInstance(nodes.get(0))) {
			
			firstChildren = (T) nodes.get(0);
		}
		
		return firstChildren;
	}
	
	public static <T> List<T> getAll(List<Node> nodes, Class<? extends T> clazz) {
		
		List<T> all = new ArrayList<>();
		int size = nodes.size();
		
		if(size > 0) {
			for(Node n : nodes) {
				if(clazz.isInstance(n)) {
					all.add((T)n);
				}
			}
		}

		return all;
		
	}
	
	public static int getNodePos(List<Node> nodes, Object node) {
		
		int pos = -1;
		
		for(int i = 0; i < nodes.size(); i++) {
			if(node==nodes.get(i)) {
				pos = i;
				break;
			}
		}
		
		return pos;
	}
}
