package pl.com.bottega.ecommerce.sales.domain.invoicing;


import com.sun.security.ntlm.Client;
import jdk.internal.org.objectweb.asm.tree.InnerClassNode;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BookKeeperTest {
    private InvoiceRequest invoiceRequest;
    private BookKeeper keeper;
    private Money money;

    @Mock
    private TaxPolicy taxPolicy;


    @Before
    public void setUp() {
        ClientData client = new ClientData(Id.generate(), "Filip");
        keeper = new BookKeeper(new InvoiceFactory());
        invoiceRequest = new InvoiceRequest(client);
        money = new Money(10);
        when(taxPolicy.calculateTax(ProductType.FOOD, money)).thenReturn(new Tax(new Money(10), "food"));
        when(taxPolicy.calculateTax(ProductType.STANDARD,money)).thenReturn(new Tax(new Money(10), "standard"));
    }

    @Test
    public void givenInvoiceRequestWithSingleItem() {
        ProductData productData = new ProductData(Id.generate(), Money.ZERO, "Product", ProductType.FOOD, new Date());
        RequestItem item = new RequestItem(productData, 1, this.money);
        invoiceRequest.add(item);
        Invoice invoice = keeper.issuance(invoiceRequest, taxPolicy);
        Assert.assertThat(invoice.getItems().size(), is(1));


    }

    @Test
    public void givenInvoiceRequestWithTwoItem() {
        ProductData productData = new ProductData(Id.generate(), Money.ZERO, "Product", ProductType.FOOD, new Date());
        ProductData productData1 = new ProductData(Id.generate(), Money.ZERO, "Product", ProductType.STANDARD, new Date());
        RequestItem item = new RequestItem(productData, 1, this.money);
        RequestItem item1 = new RequestItem(productData1, 1, this.money);
        invoiceRequest.add(item);
        invoiceRequest.add(item1);
        keeper.issuance(invoiceRequest, taxPolicy);
        verify(taxPolicy, times(1)).calculateTax(ProductType.FOOD, this.money);
        verify(taxPolicy, times(1)).calculateTax(ProductType.STANDARD,this.money);

    }

    @Test
    public void invoiceRequestWithoutItemsBeEmpty() {
        Invoice invoice = keeper.issuance(invoiceRequest, taxPolicy);
        invoice.getGros();
        Assert.assertThat(invoice.getItems().size(), is(0));
    }

    @Test
    public void invoiceRequestWithoutItemsNotCallTaxPolicy() {
        keeper.issuance(invoiceRequest, taxPolicy);
        verifyZeroInteractions(taxPolicy);
    }

    @Test
    public void invoiceBeEqualToItemsTotalCostSumm() {
        ProductData productData = new ProductData(Id.generate(), Money.ZERO, "Product", ProductType.FOOD, new Date());
        ProductData productData1 = new ProductData(Id.generate(), Money.ZERO, "Product", ProductType.STANDARD, new Date());
        RequestItem item = new RequestItem(productData, 1,this.money);
        RequestItem item1 = new RequestItem(productData1, 1, this.money);
        invoiceRequest.add(item);
        invoiceRequest.add(item1);

        Invoice invoice = keeper.issuance(invoiceRequest, taxPolicy);
        Assert.assertThat(invoice.getNet(), is(money.add(money)));
    }


}