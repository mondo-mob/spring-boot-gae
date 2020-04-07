## 3.1.1 (2020-02-25)
`AsyncDeleteRepository.deleteByKeyAsync()` was internally deleting using `.entities()` instead of `.keys()`. This did not cause an issue since the API is forgiving, but has been corrected anyway to use `.keys()`.

## Older releases
Please see the old repository that was forked: https://github.com/mondo-mob/spring-boot-gae
