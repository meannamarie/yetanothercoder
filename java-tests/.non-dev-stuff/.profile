me@mes-MacBook-Pro:~/idea-projects$ cat ~/.profile
#!/bin/bash


PATH=$PATH


alias a='alias'
alias la='ls -laG'

export CLICOLOR=1
export LSCOLORS=ExFxBxDxCxegedabagacad

export PS1="\u@\h:\w >\n$ "

export TERM="xterm-color"
export PS1='\[\033[0;33m\]\u\[\033[0m\]@\[\033[0;32m\]\h:\[\033[0m\]\[\033[0;31m\]\w \[\033[0m\] \n$ '