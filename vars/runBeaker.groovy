def call(String project, String host, String user = 'root') {
  def tpl_file = '/home/vagrant/centos-puppet.yml'
  def tpl_file_dest = "spec/acceptance/nodesets/centos-puppet-${project}.yml"
  sh("cp ${tpl_file} ${tpl_file_dest}")
  sh("echo \"gem 'beaker', :git => 'https://github.com/sathlan/beaker.git', :branch => 'feature/no-private-network'\" >> Gemfile")
  sh('bundle install --binstubs')
  sh("sed -i -e 's/%MODULE%/${project}-${env.BUILD_ID}/' ${tpl_file_dest}")
  sh("sed -i -e 's/%USER%/${user}/' ${tpl_file_dest}")
  sh("sed -i -e 's/%HOST%/${host}/' ${tpl_file_dest}")
  sh("cat ${tpl_file_dest}")
  stage "Running beaker"
  sh("env HOME=/home/vagrant BEAKER_destroy=no BEAKER_set=centos-puppet-${project} BEAKER_debug=yes ./bin/rake beaker")
}
