def call(String project, String host, String user = 'root') {
  def tpl_file = '/home/vagrant/Vagrantfile.tpl'
  sh("echo \"gem 'beaker', :git => 'https://github.com/sathlan/beaker.git', :branch => 'feature/libvirt_options'\" >> Gemfile")
  sh('bundle install --binstubs')
  sh("sed -i -e 's/%MODULE%/${project}/' ${tpl_file}")
  sh("sed -i -e 's/%USER%/${user}/' ${tpl_file}")
  sh("sed -i -e 's/%HOST%/${host}/' ${tpl_file}")
  sh("cp ${tpl_file} spec/acceptance/nodesets/centos-puppet-${project}.yml")
  sh("env BEAKER_destroy=no BEAKER_set=centos-puppet-${project} BEAKER_debug=yes ./bin/rake beaker")
}
