
/*
 * SOCKS - let's us receive socket packages from any socket. 
 *  We can use this for both time- and space-sidechannels,
 *  because we can receive it from both UDP/TCP channels, and
 *  the console/applications. 
*/

#include <iostream>
#include <sys/socket.h>
#include <sys/types.h>
#include <sys/un.h>
#include <stdio.h>
#include <stdlib.h>
#include <sstream>
#include <unistd.h>
#include <signal.h>

#define LISTEN_BACKLOG 50

static char* socket_name;
static int sfd;

void ctrlc_handler(int)
{
    close(sfd);
    unlink(socket_name);
    exit(1);
}

int main(int argc, char** argv) 
{
    if(argc < 2) 
    {
        std::cerr << "Put the socks on <SOME EXPERIMENT>." << std::endl;
        exit(0);
    }

    socket_name = argv[1];

    signal(SIGINT, ctrlc_handler);
    signal(SIGQUIT, ctrlc_handler);

    int cfd;
    sockaddr_un my_address, peer_address;
    socklen_t peer_address_size;

    int option = 1;
    sfd = socket(AF_UNIX, SOCK_STREAM, 0);
    setsockopt(sfd, SOL_SOCKET, SO_REUSEADDR, &option, sizeof(option));

    if(sfd == -1)
    {
        std::cerr << "Cannot open socket." << std::endl;
        exit(1);
    }

    memset(&my_address, 0, sizeof(sockaddr_un));
    my_address.sun_family = AF_UNIX;

    strncpy(my_address.sun_path, socket_name, sizeof(my_address.sun_path) - 1);

    if(bind(sfd, (sockaddr *) &my_address, sizeof(sockaddr_un)) == -1)
    {
        std::cerr << "Cannot bind address." << std::endl;
        exit(1);
    }

    if (listen(sfd, LISTEN_BACKLOG) == -1)
    {
        std::cerr << "Cannot listen to address." << std::endl;
        exit(1);
    }

    int len;
    char buf[100];

    while(true) {
        std::stringstream input;

        peer_address_size = sizeof(sockaddr_un);
        cfd = accept(sfd, (sockaddr *) &peer_address, &peer_address_size);

        while(len = recv(cfd, &buf, 100, 0), len > 0) {
            input << buf;
            memset(&buf[0], 0, sizeof(buf));
        }

        std::cout << input.str() << std::endl;
    }

    close(sfd);
}


