desc 'Run specs'
task :spec do
  sh 'lein spec -c'
end

desc 'Run specs verbosely'
task 'spec:verbose' do
  sh 'lein spec -c -f d'
end

desc 'Generate docs'
task :docs do
  sh 'lein marg'
end

desc 'Cleanup'
task :clean do
  sh 'lein clean'
  sh 'rm -rf docs/'
end
