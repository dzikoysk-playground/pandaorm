# Panda ORM
Object-Relational Mapping extracted from [Panda](https://github.com/panda-lang/panda) project 

#### Supported databases
- [x] InMemory database
- [x] SQL databases (without relations)
- [ ] NoSQL databases

#### Supported layers
- [x] Services
- [x] Repositories
- [x] Entities

#### Example
```java
public static void main(String... args) {
    AutomatedDataSpace space = AutomatedDataSpace.initialize(new InMemoryDataController())
            .createCollection()
                .name("users")
                .entity(User.class)
                .service(UserService.class)
                .repository(UserRepository.class)
                .append()
            .createCollection()
                .name("special-users")
                .entity(User.class)
                .service(SpecialUserService.class)
                .repository(UserRepository.class)
                .append()
            .collect();

    DataCollection collection = space.getCollection("users");
    UserService service = collection.getService(UserService.class);

    User user = service.createUser("onlypanda");
    user.setName("updated panda");

    Optional<User> foundByUser = service.findUserByName("updated panda");
    User foundById = service.findUserByNameOrId("fake username", user.getId());

    AtomicBoolean succeed = new AtomicBoolean(false);
    DataTransaction transaction = user.transaction(() -> {
                user.setName("variant panda");
                user.setName("transactional panda");
            })
            .success((attempt, time) -> {
                succeed.set(true);
            });
    transaction.commit();
}

@Service
static class SpecialUserService {

    @Autowired
    public SpecialUserService(UserService service, @Berry("special-users") UserRepository repository) {
        Assertions.assertNotEquals(repository, service.repository);
    }

}

@Service
static class UserService {

    private final UserRepository repository;

    @Autowired
    public UserService(@Berry("users") UserRepository repository) {
        this.repository = repository;
    }

    public User createUser(String name) {
        return repository.createUser(name);
    }

    public Optional<User> findUserByName(String name) {
        return repository.findUserByName(name);
    }

    public User findUserByNameOrId(String name, UUID id) {
        return repository.findByNameOrId(name, id);
    }

}

@Repository
interface UserRepository extends InMemoryDataRepository<User> {

    User createUser(@As("name") String name);

    Optional<User> findUserByName(String name);

    User findByNameOrId(String name, UUID id);

}

@Entity
public interface User extends DataEntity {

    void setName(String name);

    String getName();

    @Id
    @Generated
    UUID getId();

}
```
