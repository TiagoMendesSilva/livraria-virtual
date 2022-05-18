package br.com.uniciv.rest.livraria;

import javax.ws.rs.*;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

@Path("livro")
@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
public class LivroResource {

    private LivroRepositorio livroRepositorio = LivroRepositorio.getInstance();


    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Livros getLivros(){
        Livros livros = new Livros();
        livros.setLivros(livroRepositorio.getLivros());
        return livros;
    }

    @GET
    @Path("/{isbn}")
    public ItemBusca getLivroPorIsbn(@PathParam("isbn") String isbn){
        try{
           Livro livro = livroRepositorio.getLivroPorIsbn(isbn);
           ItemBusca itemBusca = new ItemBusca();
           itemBusca.setLivro(livro);

            Link link = Link.fromUri("/carrinho/"+livro.getId()).rel("carrinho").type("POST").build();
            itemBusca.addLink(link);
            return itemBusca;

        }catch (LivroNaoEncontradoException e){
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @POST
    public Response criaLivro(Livro livro){

        try {
            livroRepositorio.adicionaLivro(livro);
        }catch (LivroExistenteException e){
            throw new WebApplicationException(Response.Status.CONFLICT);
        }
        URI uriLocation = UriBuilder.fromPath("livro/{isbn}").build(livro.getIsbn());
        return Response.created(uriLocation).entity(livro).build();
    }

    @PUT
    @Path("/{isbn}")
    public Response atualizaLivro(@PathParam("isbn") String  isbn, Livro livro){

        try {
            Livro recuperaLivroDoRepositorio = livroRepositorio.getLivroPorIsbn(isbn);
            recuperaLivroDoRepositorio.setAutor(livro.getAutor());
            recuperaLivroDoRepositorio.setGenero(livro.getGenero());
            recuperaLivroDoRepositorio.setPreco(livro.getPreco());
            recuperaLivroDoRepositorio.setTitulo(livro.getTitulo());

            livroRepositorio.atualizaLivro(recuperaLivroDoRepositorio);
        }catch (LivroNaoEncontradoException e){
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return Response.ok().entity(livro).build();
    }

    @DELETE
    @Path("/{id}")
    public  void removeLivro(@PathParam("id") Long id){
        try {
            livroRepositorio.removeLivro(id);
        }catch (LivroNaoEncontradoException e){
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }


}
