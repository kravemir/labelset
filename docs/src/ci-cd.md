# CI / CD

Lablie is built also as a docker image, and published on [dockerhub][dockerhub-lablie].

## Usage with GitLab Runner

Example of `.gitlab-ci.yml`:

```yaml
image: kravemir/lablie:0.4.0

# install custom fonts used in templates for correct PDF generation
# set locale for international characters
before_script:
  - apk add ttf-dejavu ttf-liberation ttf-linux-libertine texmf-dist-fontsextra ghostscript-fonts
  - mkdir -p ~/.fonts; ln -s /usr/share/texmf-dist/fonts/opentype/ ~/.fonts/
  - fc-cache -v -f
  - fc-list
  - export LANG=en_US.UTF-8
  - export LANGUAGE=en_US:en
  - export LC_ALL=en_US.UTF-8

# artwork job generating outputs
artwork:
  script:
    - make
  artifacts:
    paths:
      - output
```

[dockerhub-lablie]: https://hub.docker.com/r/kravemir/lablie 
