package com.todoapp.demo.service;

import com.todoapp.demo.domain.Tag;
import com.todoapp.demo.domain.User;
import com.todoapp.demo.repository.TagRepository;
import com.todoapp.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    // veritabanıyla konuşmak için kullanılan repository katmanları:
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    public TagService(TagRepository tagRepository, UserRepository userRepository) {
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
    }
    // kullanıcı için yeni tag oluşturur, kullanıcıyı bulur, bulamazsa hata fırlatır
    // RuntimeException("User not found)
    // yeni tag nesnesi oluşturur, tag ile kullanıcıyla ilişkilendirir (Many-to-One ilişki)
    // tagRepository.save(tag) ile veritabanına ekler.
    // mantık olarak: "bu kullanıcı için şu isimde bir tag oluştur"
    // POST /tags endpointine şöyle bir JSON gider:
    // { "name": "Work"}
    // controller bunu tagService.createTag("Work", principal.getName()) şeklinde çağırır.
    // service metodu kullanıcıyı bulur, Tag("Work", user) nesnesini oluşturur ve kaydeder.
    public Tag createTag(String tagName, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Tag tag = new Tag();
        tag.setName(tagName);
        tag.setUser(user);

        return tagRepository.save(tag);
    }
    // username'e göre kullanıcı bulur
    // sonra bu kullanıcıya ait tüm tagleri tagRepository.findByUser(user) ile getirir.
    // bu method GET /tags endpointinin arkasında çalışıyor. her kullanıcı
    // sadece kendi etiketini görebilir.
    // genel akış controller -> service -> repository -> database
    public List<Tag> getTagsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return tagRepository.findByUser(user);
    }
}
