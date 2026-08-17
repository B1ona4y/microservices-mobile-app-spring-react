# Changelog

## [1.6.0](https://github.com/B1ona4y/microservices-mobile-app-spring-react/compare/v1.5.0...v1.6.0) (2026-08-17)


### Features

* cascade deletion of pages ([04886ff](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/04886ff30009cb07589669609dbdc2fdd1f8767b))
* lib got new methods to separate actions before and anfter sync happens ([925ad1a](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/925ad1a87baeaf84d423200f864c0d699d63be53))

## [1.5.0](https://github.com/B1ona4y/microservices-mobile-app-spring-react/compare/v1.4.0...v1.5.0) (2026-08-14)


### Features

* Notebooks id type change from String to UUID with generation style TIME ([8cd138d](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/8cd138d7409cd9c76924a1fd0457a1d618777635))
* oauth2 id type changed from Long to UUID with Generation style TIME ([6297486](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/6297486acdfdedaf076bfc1cd3195dd0eeeec34a))
* Profile id type change from String to UUID with generation style TIME ([47b770d](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/47b770d54024136015242f1d916bfb7a3cfddb8d))
* refresh token id type change from Long/string to UUID with generation style TIME ([e318807](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/e318807109c8bd08cde61089dc9ca105da26c455))

## [1.4.0](https://github.com/B1ona4y/microservices-mobile-app-spring-react/compare/v1.3.1...v1.4.0) (2026-08-09)


### Features

* added bio to response ([b7997d9](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/b7997d92fa9e89829f7a08abd75afb946605009b))
* adding profiles to gateway ([e4ba3dc](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/e4ba3dcdb9db4b6c9af8649e9d97751b6ec2f39f))
* additional error handling ([7b300d5](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/7b300d5de66ada1c29e8d1948e17518fb70107d0))
* additional service methods ([ee6292f](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/ee6292fd4454d3e9b405120a9d1e009f87ae2d31))
* agent for writing tests ([b4c74ba](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/b4c74ba9097172b8d7d51bac9bece6263f280a72))
* basic structure for userProfile entity ([24d8659](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/24d86597fafad1cb2f4470e76685490a357b9542))
* better controller error handling ([ec86ef7](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/ec86ef7a1ae72ea04d42323be47141be11030d01))
* better path structure ([65bb7c5](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/65bb7c5fa6f3e0eb74d862aad681b572c538f600))
* better service error handling ([e35c743](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/e35c7437ddd3ad6d72294b01371062b07b68ddf8))
* changed createProfile to createIfAbsent to take user request to build userProfile if there is none ([bfb3ff2](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/bfb3ff25a5f4ea4f64dfc01889cf499adc922f46))
* changed createProfile to createIfAbsent to take user request to build userProfile if there is none ([81254ec](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/81254eccabf957a7fdc6dc49f40a5da2dcb00100))
* controller with /profile/me to get userProfile and /profile for get or create userProfile ([1d15fa5](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/1d15fa59b5191b9390a32f541ca79661ebd5f89c))
* correct annotations ([62e3475](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/62e3475817bd59b06ba6af8ae646ba9977f59633))
* correct dto validation ([ebb17d5](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/ebb17d5e21995e63a4503f655ec16c4c5bcb3fda))
* created a enum for upsertresult so controller could send 200 and 201 codes. ([f1bd7fb](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/f1bd7fb3e612ffae1c250da5fccea15704835037))
* docker compose for profile service and h2 test ([c051263](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/c051263b5a8d7abd4a7b9f547189c47225e5e642))
* exeption for "user not found" ([3ab2652](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/3ab26527e5e1da62771f29dd3ab55f4c2ecc7def))
* global error handaling class ([7f71fe5](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/7f71fe56d212286c05f7d48b21ea6dfba52661c8))
* minio as storage for imges of user profile pictures ([7c6945e](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/7c6945e7db6254dd7622f21ed07a5645ae5cfc6c))
* refactor UserProfile and UserProfileService for correct upsert impl ([dc928c5](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/dc928c5309c4f02b9acd555c820398c982380a53))
* security config ([b3291a4](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/b3291a4b44aee86288fa5e66902903e8db9bcf59))
* tests for profile service ([1a20b0d](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/1a20b0d1fa1da4edb6f631d3b0582c80fa94e8f6))
* update and correct naming ([4582a45](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/4582a45d6726ed546a1a08e12534de2f705cabe9))
* upsert for Put endpoint ([0278da5](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/0278da5b0d35a79c8d66721856cbcc2b25e5d08d))


### Bug Fixes

* builder not superbuilder ([ed91b1b](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/ed91b1b618ce53292e337d1857464bdebbf3b344))
* DTO @Size vs @Column length mismatch ([dee3e28](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/dee3e286ee3896bdfda64eeeb6aa84c40f74d63e))
* removed id and version from request dto ([9526e55](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/9526e554978e5558fb21d284716caa5d8c15ccdb))

## [1.3.1](https://github.com/B1ona4y/microservices-mobile-app-spring-react/compare/v1.3.0...v1.3.1) (2026-07-31)


### Bug Fixes

* jwt identity is now connected to user id ([0530d6b](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/0530d6be1526eabdd1f306d5bf257c0d65f268b1))
* jwt identity is now connected to user id ([5e01e34](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/5e01e343e1e481caf31ca1242eb2a533863ec762))

## [1.3.0](https://github.com/B1ona4y/microservices-mobile-app-spring-react/compare/v1.2.0...v1.3.0) (2026-07-30)


### Features

* button for sync ([4eb02b2](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/4eb02b2e188a803101149c6ad496f1cd75cfdc07))
* crud for notebooks and pages ([aa3f2d9](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/aa3f2d9e12455821c89258048c9f08da44f7de95))
* database schema ([2e23913](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/2e239134bd7c26822a55cde8e41bb63e58173fa4))
* function for: getting dirty lines from db,  formating rows into readable payloads ([19a3569](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/19a35697b7390695a45b88a2d3c4b989593b210a))
* hooks ([fc7d9a6](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/fc7d9a6abcea6e90da107d36eeda5087173d161a))
* packages for sqlite ([21d8440](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/21d844082f3aa23c2db614005eb68549dc06dfb0))
* sync function ([ea1a35f](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/ea1a35f23f5a54c4c5695c85db9293443b854976))
* sync hook ([1f0c495](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/1f0c49509bf967dc665ad432f3c6d946fcc37a51))
* types ([a0b271f](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/a0b271f4f1692b503e04097b1203c2d0e54764b1))
* update for offline mode ([d27d095](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/d27d09546720c5fcb0751254057692447de7c08a))
* validation for names ([e05e32d](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/e05e32d2e703f0ca49f30ca37fa6bfe5641db069))


### Bug Fixes

* payloads func should not be async ([ea77f48](https://github.com/B1ona4y/microservices-mobile-app-spring-react/commit/ea77f48ff50a3dc534f298f25a0628de308bb8f8))

## [1.2.0](https://github.com/B1ona4y/login_auth2_component/compare/v1.1.0...v1.2.0) (2026-07-22)


### Features

* batching and pagination ([d07af95](https://github.com/B1ona4y/login_auth2_component/commit/d07af956ce2026faaedd14af2aafacbed97e5ed4))
* cicd up to date ([335d865](https://github.com/B1ona4y/login_auth2_component/commit/335d8652c5f829da9ddf6867e007add73f3e45c2))
* cicd up to date ([25b36de](https://github.com/B1ona4y/login_auth2_component/commit/25b36de22aac52d64c9efed12677f15c56671480))
* dtos for sync module ([ad73493](https://github.com/B1ona4y/login_auth2_component/commit/ad73493ae8fc4a7e1f6379df68e1cf55aaa777ed))
* Entities ([032a5d8](https://github.com/B1ona4y/login_auth2_component/commit/032a5d8790791b8eac7b790fb7d23449773be8da))
* init  for SyncController ([b628468](https://github.com/B1ona4y/login_auth2_component/commit/b6284681380bfad7321c0e6a25af977c79b727f3))
* notebook dto ([599ea7e](https://github.com/B1ona4y/login_auth2_component/commit/599ea7e32dc5470955ae19ef3a1ecc92eb3e5d4b))
* notebook mapper repo and service ([ae1beb8](https://github.com/B1ona4y/login_auth2_component/commit/ae1beb8553a41975eac3f5c94b64dc874fd99f9e))
* notebook security config for jwt ([eafe076](https://github.com/B1ona4y/login_auth2_component/commit/eafe076513af81af1f62befaf1cce70f9c8c7ead))
* notebook stuff ([62abe94](https://github.com/B1ona4y/login_auth2_component/commit/62abe945cc0304f8ca695d72200fe11d2b81d1ec))
* notebook stuff ([b93f54f](https://github.com/B1ona4y/login_auth2_component/commit/b93f54f1f43ad7c37b386ace9aeb41ac623f0ece))
* page dto and mapper ([2aa7394](https://github.com/B1ona4y/login_auth2_component/commit/2aa7394dd33a8da919de996439972a896689c51f))
* page stuff ([b79f513](https://github.com/B1ona4y/login_auth2_component/commit/b79f51391acb5e3efcb3a169552b208d5d1d2ac3))
* sync data ([1b908c2](https://github.com/B1ona4y/login_auth2_component/commit/1b908c2299c2ec2bebb652ec5778c8aafc4e9c7f))
* sync structure ([3e00a79](https://github.com/B1ona4y/login_auth2_component/commit/3e00a79975e6827c853bc34265e5c4b18ff9d723))
* validation for fields in sync ([e86412b](https://github.com/B1ona4y/login_auth2_component/commit/e86412be6c5e0320a58f629233aee6e1d74cba76))


### Bug Fixes

* annotation fixes ([64287c6](https://github.com/B1ona4y/login_auth2_component/commit/64287c6132cec0dc282cfdab71502e733b77e874))
* fix stuff ([484b1e9](https://github.com/B1ona4y/login_auth2_component/commit/484b1e921747471eab34eae2ee5ed2a3f70860fa))
* nullable false on updateAt + indexation for pages ([5f9b071](https://github.com/B1ona4y/login_auth2_component/commit/5f9b071085512f0413e694cf2c96ee8808720596))

## [1.1.0](https://github.com/B1ona4y/login_auth2_component/compare/v1.0.0...v1.1.0) (2026-07-17)


### Features

* compose update ([609dba7](https://github.com/B1ona4y/login_auth2_component/commit/609dba714518e16bbd0af1d6bdaa10aaf049b111))
* gateway between user and services ([c6983db](https://github.com/B1ona4y/login_auth2_component/commit/c6983db360e81c03014306eae9c8c3c113b8f947))
* gateway spring package init ([a5b2070](https://github.com/B1ona4y/login_auth2_component/commit/a5b2070b854365f63b6dcc253a03dc4085dadb6d))

## 1.0.0 (2026-07-16)


### Features

* add CI/CD pipeline with checkstyle, eslint and automated releases ([752a196](https://github.com/B1ona4y/login_auth2_component/commit/752a1969c80b54bb35ab88b3500f2c8ecd46cc2a))
* dependencies for postgres ([dd03c30](https://github.com/B1ona4y/login_auth2_component/commit/dd03c303c4af99140cf6c7579993685d5103d367))
