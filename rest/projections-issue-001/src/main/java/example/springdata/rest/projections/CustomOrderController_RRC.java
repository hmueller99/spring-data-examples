package example.springdata.rest.projections;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Repository Controller that offers custom query and data logic. 
 * Controller is annotated with {@link RepositoryRestController}, which is considered bad practice.
 * @author hmueller
 *
 */
@RepositoryRestController
public class CustomOrderController_RRC {
	

	@Autowired OrderRepository repository;

	@ResponseBody
	@RequestMapping(value="orders/rrc/collect", method=RequestMethod.GET)
	public Resources<Resource<?>> queryOrderCollection(PersistentEntityResourceAssembler assembler) {
		Iterable<Order> data = repository.findAll();
		List<Resource<?>> response = new ArrayList<Resource<?>>();
		for (Order order : data) {
			response.add(assembler.toResource(order));
		}
		
		return new Resources<Resource<?>>(response);
	}
}
