package pl.com.bottega.ecommerce.sales.application.api.handler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;

import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;

import pl.com.bottega.ecommerce.sharedkernel.Money;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.system.application.SystemContext;


import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
@RunWith(MockitoJUnitRunner.class)
public class AddProductCommandHandlerTest {
    private AddProductCommandHandler addProductCommandHandler;
    private AddProductCommand command;
    private ReservationRepository reservationRepository;
    private ProductRepository productRepository;
    private SuggestionService suggestionService;
    private ClientRepository clientRepository;
    private SystemContext systemContext;
    private Reservation reservation;
    private Product product;
    @Before
    public void setUp() {
        reservationRepository = mock(ReservationRepository.class);
        productRepository = mock(ProductRepository.class);
        suggestionService = mock(SuggestionService.class);
        clientRepository = mock(ClientRepository.class);
        systemContext = mock(SystemContext.class);
        addProductCommandHandler = new AddProductCommandHandler();
        command = new AddProductCommand(Id.generate(),Id.generate(),3);
        reservation = new Reservation(Id.generate(), Reservation.ReservationStatus.OPENED,new ClientData(Id.generate(),"Name"),new Date());
        product = new Product(Id.generate(), new Money(10), "product", ProductType.STANDARD);

        when(reservationRepository.load(command.getOrderId())).thenReturn(reservation);
        when(productRepository.load(command.getProductId())).thenReturn(product);

        Whitebox.setInternalState(addProductCommandHandler, "reservationRepository", reservationRepository);
        Whitebox.setInternalState(addProductCommandHandler, "clientRepository", clientRepository);
        Whitebox.setInternalState(addProductCommandHandler, "productRepository", productRepository);
        Whitebox.setInternalState(addProductCommandHandler, "suggestionService", suggestionService);
        Whitebox.setInternalState(addProductCommandHandler, "systemContext", systemContext);

    }

    @Test
    public void reservationRepositoryCalledAtLeastOnce() {
        when(reservationRepository.load(Id.generate())).thenReturn(null);
        addProductCommandHandler.handle(command);
        verify(reservationRepository, times(1)).load(any());
    }
    @Test
    public void suggestionServiceMethodNotBeCalledForEmptyComand() {
        addProductCommandHandler.handle(command);
        verify(suggestionService, never()).suggestEquivalent(any(Product.class), any(Client.class));
    }
    @Test
    public void methodSaveShouldBeCalledOnes() {
        final int testValue = 1;

        addProductCommandHandler.handle(command);

        verify(reservationRepository, times(testValue)).save(reservation);
    }

}