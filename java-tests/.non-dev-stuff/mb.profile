me@mes-MacBook-Pro:~/idea-projects$ cat ~/.profile
#!/bin/bash

alias a='alias'
alias l='ls -lahG'
alias texted='open -a TextEdit'

export M2_HOME=/usr/local/apache-maven-3.0.4
export M2=$M2_HOME/bin
#export JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/Current/

export CLICOLOR=1
export LSCOLORS=ExFxBxDxCxegedabagacad

export TERM="xterm-color"
export PS1='\[\033[0;33m\]\u\[\033[0m\]@\[\033[0;32m\]\h:\[\033[0m\]\[\033[0;31m\]\w \[\033[0m\] \n$ '

PATH=$PATH:.:/opt/scala-2.9.2/bin:/usr/local/bin:$M2:~/android.sdk/platform-tools:~/android.sdk/tools


# me.ubuntu >>

export JAVA_HOME=/usr/lib/jvm/jdk1.6.0_33
export M2_HOME=/usr/local/maven/3.0.4
export M2=$M2_HOME/bin

export EDITOR=vim
export ANDROID_SDK=/usr/local/android.sdk

export PATH="$HOME/bin:$PATH:$HOME/IDEA/idea-IC-117.418/bin/:$M2:$ANDROID_SDK/tools"


alias a='alias'
a l='ls -alF'
a la='ls -A'
a ll='ls -CF'
a bashrc='vim $HOME/.bashrc'
