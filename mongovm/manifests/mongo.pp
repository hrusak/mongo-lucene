# http://docs.puppetlabs.com/references/latest/type.html

Exec { path => ["/bin/", "/sbin/", "/usr/bin/", "/usr/sbin/", "/usr/local/bin"] }
File { owner => 0, group => 0, mode => 0644 }

class apt {
    file {"/etc/apt/sources.list.d/10gen.list":
        content => "deb http://downloads-distro.mongodb.org/repo/debian-sysvinit dist 10gen"
    }
	exec {"apt-get_update":
        command => "apt-get update",
        require => File["/etc/apt/sources.list.d/10gen.list"]
    }
}

class ssh {
    package { "openssh-server":
        ensure => present,
        require => Class["apt"]
    }
}

class git {
    package { "git":
        ensure => present,
        require => Class["apt"]
    }
}

class mongo {
    exec { "mongodb20-10gen":
        require => Class["apt"],
        command => "apt-get -y -q --force-yes install mongodb20-10gen"
    }
    service { "mongod":
        ensure => running,
        require => Exec["mongodb20-10gen"]
    }
}

include apt
include git
include ssh
include mongo
