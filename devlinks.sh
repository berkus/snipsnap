#! /bin/sh
base=`pwd`
app=$base/applications/$1
theme=$2
if [ "$1" = "" -o ! -d $app ]; then
  echo "usage: $0 AppName [theme]"
  exit
fi

if [ "$theme" = "" -o ! -d $base/src/theme/$theme ]; then
  theme=blue
fi
if [ -d $app ]; then
  echo Linking code ...
  ln -sf $base/src/apps/default/WEB-INF/*.tld $app/WEB-INF/
  ln -sf $base/src/apps/default/WEB-INF/web.xml $app/WEB-INF/
  ln -sf $base/src/apps/default/WEB-INF/lib/*.jar $app/WEB-INF/lib
  echo Linking JSPs ...
  ln -sf $base/src/apps/default/*.jsp $app/
  ln -sf $base/src/apps/default/util/*.jsp $app/util/
  ln -sf $base/src/apps/default/admin/*.jsp $app/admin/
  echo Linking theme $theme ...
  ln -sf $base/src/theme/$theme/css/*.css $app/css/
  ln -sf $base/src/theme/$theme/images/*.* $app/images/
fi