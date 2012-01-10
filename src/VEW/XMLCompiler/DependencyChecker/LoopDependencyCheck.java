package VEW.XMLCompiler.DependencyChecker;

import java.util.ArrayList;
import java.util.Collection;

public class LoopDependencyCheck<D extends HasDependency> extends GraphDependencyCheck<D> {

	@Override
	protected void internalCheckDependencies() {
		
		for (Representative<D> r : this.getRepresentatives()) {
			r.setVisited(false);
		}
		
		for (Representative<D> r : this.getRepresentatives()) {
			if (!r.hasBeenVisited()) {
				Collection<Collection<Representative<D>>> failed = new LoopVisitor<D>(r).startLoop();
				if (failed != null && !failed.isEmpty()) {
					this.failedDependencies.addAll(failed);
				}
			}
		}
		
	}
	
	
 	private class LoopVisitor<E extends HasDependency> {
	 
		private Representative<E> start;

		public LoopVisitor(Representative<E> start) {
			this.start = start;
		}
		
		public Collection<Collection<Representative<E>>> startLoop() {
			
			Collection<Representative<E>> currentChain = new ArrayList<Representative<E>> ();
			
			return depthSearch(start, currentChain);
			
		}
		
		private Collection<Collection<Representative<E>>> depthSearch(Representative<E> current, 
					Collection<Representative<E>> currentChain) {
			
			currentChain.add(current);
			current.setVisited(true);
			
			Collection<Representative<E>> cycle = checkCurrentForCycle(currentChain);
			if (cycle != null) {
				ArrayList<Collection<Representative<E>>> result = new ArrayList<Collection<Representative<E>>> ();
				result.add(cycle);
				return result;
			}
			
			Collection<Collection<Representative<E>>> result = new ArrayList<Collection<Representative<E>>> ();
			for (Representative<E> child : current.getChildren()) {
				Collection<Collection<Representative<E>>> failed = depthSearch(child, currentChain);
				if (failed != null) {
					result.addAll(failed);
				}
			}
			
			return (result.isEmpty() ? null : result);
		}
		
		/*
		private Collection<Collection<Representative<E>>> depthSearch(Representative<E> current,
																	  Collection<Representative<E>> currentChain) {
			currentChain.add(current);
			
			Collection<Representative<E>> cycle = checkCurrentForCycle(currentChain);
			if (cycle != null) {
				ArrayList<Collection<Representative<E>>> result = new ArrayList<Collection<Representative<E>>> ();
				result.add(cycle);
				return result;
			}
			
			if (current.hasBeenVisited()) {
				return null;
			}
			
			current.setVisited(true);
			
			Collection<Representative<E>> children = current.getChildren();
			
			if (children.isEmpty()) {
				return null;
			}
			
			while (children.size() == 1) {
				Representative<E> next = children.iterator().next();
				
				if (next.hasBeenVisited()) {
					return null;
				}
				
				cycle = checkCurrentForCycle(currentChain);
				if (cycle != null) {
					ArrayList<Collection<Representative<E>>> result = new ArrayList<Collection<Representative<E>>> ();
					result.add(cycle);
					return result;
				}
				
				current = next;
				current.setVisited(true);
				currentChain.add(current);
				
			}
			
			Collection<Collection<Representative<E>>> result = new ArrayList<Collection<Representative<E>>> ();
			for (Representative<E> child : current.getChildren()) {
				Collection<Collection<Representative<E>>> failed = depthSearch(child, currentChain);
				if (failed != null) {
					result.addAll(failed);
				}
			}
			
			return (result.isEmpty() ? null : result);
		}
		*/

		/**
		 * 
		 * @param currentChain the current chain of nodes from start to end
		 * @return the actual loop in the code
		 */
		private Collection<Representative<E>> checkCurrentForCycle(Collection<Representative<E>> currentChain) {
			
			Representative<E> chainLoop = null;
			outer: for (Representative<E> rep1 : currentChain) {
				boolean found = false;
				for (Representative<E> rep2 : currentChain) {
					if (rep1 == rep2) {
						if (found) {
							chainLoop = rep1;
							break outer;
						} else {
							found = true;
						}
						
					}
				}
			}
			
			if (chainLoop == null) {
				return null;
			} else {
				Collection<Representative<E>> result = new ArrayList<Representative<E>> ();
				boolean collecting = false;
				for (Representative<E> rep : currentChain) {
					if (collecting) {
						result.add(rep);
					} else {
						// if !collecting then see if at the start of the loop 
						collecting = rep == chainLoop;
					}
				}
				return result;
			}
			
			
		}
		
		
	}
}
